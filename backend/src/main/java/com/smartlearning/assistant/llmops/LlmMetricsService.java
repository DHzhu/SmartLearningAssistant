package com.smartlearning.assistant.llmops;

import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LlmMetricsService {

    private final LlmTraceRepository traceRepository;

    public LlmMetricsService(LlmTraceRepository traceRepository) {
        this.traceRepository = traceRepository;
    }

    public record AggregatedMetrics(
            long totalCalls,
            double successRate,
            double avgTotalLatencyMs,
            double avgRetrievalLatencyMs,
            long totalTokens,
            List<LlmTraceRecord> recentTraces) {}

    public LlmTraceRecord recordTrace(LlmTraceRecord record) {
        return traceRepository.save(record);
    }

    @Async
    public void recordTraceAsync(LlmTraceRecord record) {
        traceRepository.save(record);
    }

    public AggregatedMetrics getAggregatedMetrics() {
        long total = traceRepository.count();
        if (total == 0) {
            return new AggregatedMetrics(0L, 100.0, 0.0, 0.0, 0L, List.of());
        }

        long successCount = traceRepository.countByStatus("SUCCESS");
        double successRate = ((double) successCount / total) * 100.0;

        List<LlmTraceRecord> all = traceRepository.findAll();
        double avgTotalLatency = all.stream().mapToLong(LlmTraceRecord::getTotalLatencyMs).average().orElse(0.0);
        double avgRetrievalLatency = all.stream().mapToLong(LlmTraceRecord::getRetrievalLatencyMs).average().orElse(0.0);
        long totalTokens = all.stream().mapToLong(r -> r.getPromptTokens() + r.getCompletionTokens()).sum();

        List<LlmTraceRecord> recent = traceRepository.findTop50ByOrderByCreatedAtDesc();

        return new AggregatedMetrics(
                total,
                Math.round(successRate * 100.0) / 100.0,
                Math.round(avgTotalLatency * 100.0) / 100.0,
                Math.round(avgRetrievalLatency * 100.0) / 100.0,
                totalTokens,
                recent);
    }
}
