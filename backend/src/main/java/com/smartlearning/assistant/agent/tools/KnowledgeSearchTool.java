package com.smartlearning.assistant.agent.tools;

import com.smartlearning.assistant.chat.RagService;
import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeSearchTool {

    private final RagService ragService;

    public KnowledgeSearchTool(RagService ragService) {
        this.ragService = ragService;
    }

    public record SearchRequest(String query, Long userId) {}

    public record SearchResponse(int matchCount, List<String> documents) {}

    public SearchResponse searchKnowledge(SearchRequest request) {
        List<Document> docs = ragService.retrieveContext(request.query(), request.userId());
        List<String> formatted = docs.stream()
                .map(doc -> {
                    String filename = (String) doc.getMetadata().getOrDefault("filename", "unknown");
                    return "[" + filename + "] " + doc.getText();
                })
                .toList();

        return new SearchResponse(formatted.size(), formatted);
    }
}
