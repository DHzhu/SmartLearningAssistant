package com.smartlearning.assistant.llmops;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sys_llm_trace")
public class LlmTraceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trace_id", nullable = false, length = 64)
    private String traceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action_type", nullable = false, length = 32)
    private String actionType; // CHAT_RAG, AGENTIC_CHAT, EMBEDDING

    @Column(name = "model_name", nullable = false, length = 64)
    private String modelName;

    @Column(name = "retrieval_latency_ms", nullable = false)
    private long retrievalLatencyMs;

    @Column(name = "total_latency_ms", nullable = false)
    private long totalLatencyMs;

    @Column(name = "prompt_tokens", nullable = false)
    private long promptTokens;

    @Column(name = "completion_tokens", nullable = false)
    private long completionTokens;

    @Column(nullable = false, length = 32)
    private String status = "SUCCESS"; // SUCCESS, FAILED

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public LlmTraceRecord() {}

    public LlmTraceRecord(
            String traceId,
            Long userId,
            String actionType,
            String modelName,
            long retrievalLatencyMs,
            long totalLatencyMs,
            long promptTokens,
            long completionTokens,
            String status,
            String errorMessage) {
        this.traceId = traceId;
        this.userId = userId;
        this.actionType = actionType;
        this.modelName = modelName;
        this.retrievalLatencyMs = retrievalLatencyMs;
        this.totalLatencyMs = totalLatencyMs;
        this.promptTokens = promptTokens;
        this.completionTokens = completionTokens;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTraceId() { return traceId; }
    public Long getUserId() { return userId; }
    public String getActionType() { return actionType; }
    public String getModelName() { return modelName; }
    public long getRetrievalLatencyMs() { return retrievalLatencyMs; }
    public long getTotalLatencyMs() { return totalLatencyMs; }
    public long getPromptTokens() { return promptTokens; }
    public long getCompletionTokens() { return completionTokens; }
    public String getStatus() { return status; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
