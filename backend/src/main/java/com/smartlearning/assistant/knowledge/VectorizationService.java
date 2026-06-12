package com.smartlearning.assistant.knowledge;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class VectorizationService {

    private static final Logger log = LoggerFactory.getLogger(VectorizationService.class);

    private final KnowledgeTaskRepository taskRepository;
    private final Optional<VectorStore> vectorStore;

    public VectorizationService(
            KnowledgeTaskRepository taskRepository,
            Optional<VectorStore> vectorStore) {
        this.taskRepository = taskRepository;
        this.vectorStore = vectorStore;
    }

    @Async("knowledgeTaskExecutor")
    public void processTask(Long taskId) {
        KnowledgeTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        task.setStatus(KnowledgeTask.Status.PROCESSING);
        taskRepository.save(task);

        try {
            if (vectorStore.isEmpty()) {
                throw new IllegalStateException("VectorStore not configured");
            }

            String text = extractTextFromS3(task.getS3Url());
            List<String> chunks = splitText(text);

            List<Document> documents = chunks.stream()
                    .map(chunk -> new Document(chunk,
                            java.util.Map.of(
                                    "userId", task.getUserId().toString(),
                                    "taskId", taskId.toString(),
                                    "filename", task.getFilename())))
                    .toList();

            vectorStore.get().add(documents);

            task.setStatus(KnowledgeTask.Status.SUCCESS);
            task.setChunkCount(chunks.size());
            taskRepository.save(task);

            log.info("Task {} completed with {} chunks", taskId, chunks.size());
        } catch (Exception e) {
            log.error("Task {} failed: {}", taskId, e.getMessage(), e);
            task.setStatus(KnowledgeTask.Status.FAILED);
            task.setErrorMessage(e.getMessage());
            taskRepository.save(task);
        }
    }

    private String extractTextFromS3(String s3Url) {
        return "Extracted text from " + s3Url;
    }

    private List<String> splitText(String text) {
        int chunkSize = 500;
        java.util.List<String> chunks = new java.util.ArrayList<>();
        for (int i = 0; i < text.length(); i += chunkSize) {
            chunks.add(text.substring(i, Math.min(i + chunkSize, text.length())));
        }
        return chunks;
    }

    public List<Document> searchSimilar(String query, Long userId, int topK) {
        if (vectorStore.isEmpty()) {
            return List.of();
        }

        SearchRequest request = SearchRequest.builder()
                .query(query)
                .topK(topK)
                .build();

        return vectorStore.get().similaritySearch(request);
    }
}
