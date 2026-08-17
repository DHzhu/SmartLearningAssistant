package com.smartlearning.assistant.chat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smartlearning.assistant.chat.rerank.RerankerService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private ChatModel chatModel;
    @Mock
    private VectorStore vectorStore;
    @Mock
    private RerankerService rerankerService;

    private RagService ragService;

    @BeforeEach
    void setUp() {
        ragService = new RagService(Optional.of(chatModel), Optional.of(vectorStore), rerankerService);
    }

    @Test
    void shouldRetrieveAndRerankDocumentsForUser() {
        Document doc1 = new Document("Doc 1", Map.of("userId", "1"));
        Document doc2 = new Document("Doc 2 (other user)", Map.of("userId", "2"));
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(doc1, doc2));
        when(rerankerService.rerank(eq("query"), anyList(), eq(3))).thenReturn(List.of(doc1));

        List<Document> result = ragService.retrieveContext("query", 1L);

        assertEquals(1, result.size());
        assertEquals("Doc 1", result.get(0).getText());
        verify(rerankerService).rerank(eq("query"), argThat(list -> list.size() == 1), eq(3));
    }

    @Test
    void shouldBuildPromptWithContext() {
        Document doc = new Document("Spring AI is awesome", Map.of("filename", "intro.txt"));
        String prompt = ragService.buildPrompt("what is spring ai?", List.of(doc));

        assertTrue(prompt.contains("Spring AI is awesome"));
        assertTrue(prompt.contains("what is spring ai?"));
    }

    @Test
    void shouldBuildRawPromptWhenContextEmpty() {
        String prompt = ragService.buildPrompt("simple query", List.of());
        assertEquals("simple query", prompt);
    }
}
