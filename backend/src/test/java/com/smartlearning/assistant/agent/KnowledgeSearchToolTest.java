package com.smartlearning.assistant.agent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.smartlearning.assistant.agent.tools.KnowledgeSearchTool;
import com.smartlearning.assistant.chat.RagService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

@ExtendWith(MockitoExtension.class)
class KnowledgeSearchToolTest {

    @Mock
    private RagService ragService;

    private KnowledgeSearchTool knowledgeSearchTool;

    @BeforeEach
    void setUp() {
        knowledgeSearchTool = new KnowledgeSearchTool(ragService);
    }

    @Test
    void shouldSearchAndFormatDocuments() {
        Document doc = new Document("Spring AI provides tool calling.", Map.of("filename", "spring_ai.pdf"));
        when(ragService.retrieveContext("spring ai", 1L)).thenReturn(List.of(doc));

        KnowledgeSearchTool.SearchResponse response = knowledgeSearchTool.searchKnowledge(new KnowledgeSearchTool.SearchRequest("spring ai", 1L));

        assertNotNull(response);
        assertEquals(1, response.matchCount());
        assertTrue(response.documents().get(0).contains("spring_ai.pdf"));
    }

    @Test
    void shouldHandleEmptyResults() {
        when(ragService.retrieveContext("unknown", 1L)).thenReturn(List.of());

        KnowledgeSearchTool.SearchResponse response = knowledgeSearchTool.searchKnowledge(new KnowledgeSearchTool.SearchRequest("unknown", 1L));

        assertNotNull(response);
        assertEquals(0, response.matchCount());
        assertTrue(response.documents().isEmpty());
    }
}
