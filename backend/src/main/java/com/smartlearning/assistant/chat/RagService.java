package com.smartlearning.assistant.chat;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

@Service
public class RagService {

    private static final Logger log = LoggerFactory.getLogger(RagService.class);
    private static final int TOP_K = 3;

    private final Optional<ChatModel> chatModel;
    private final Optional<VectorStore> vectorStore;

    public RagService(Optional<ChatModel> chatModel, Optional<VectorStore> vectorStore) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
    }

    public List<Document> retrieveContext(String query, Long userId) {
        if (vectorStore.isEmpty()) {
            return List.of();
        }

        try {
            var searchRequest = org.springframework.ai.vectorstore.SearchRequest.builder()
                    .query(query)
                    .topK(TOP_K)
                    .build();

            List<Document> results = vectorStore.get().similaritySearch(searchRequest);

            return results.stream()
                    .filter(doc -> {
                        Object docUserId = doc.getMetadata().get("userId");
                        return docUserId != null && String.valueOf(userId).equals(docUserId.toString());
                    })
                    .toList();
        } catch (Exception e) {
            log.warn("Vector search failed for user {}: {}", userId, e.getMessage());
            return List.of();
        }
    }

    public String buildPrompt(String query, List<Document> context) {
        if (context.isEmpty()) {
            return query;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("基于以下参考资料回答用户问题。如果参考资料中没有相关信息，请说明。\n\n");
        sb.append("参考资料：\n");

        for (int i = 0; i < context.size(); i++) {
            sb.append(i + 1).append(". ").append(context.get(i).getText()).append("\n");
        }

        sb.append("\n用户问题：").append(query);
        return sb.toString();
    }

    public String chat(String prompt) {
        if (chatModel.isEmpty()) {
            throw new IllegalStateException("ChatModel not configured");
        }

        return ChatClient.create(chatModel.get())
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    public boolean isAvailable() {
        return chatModel.isPresent();
    }
}
