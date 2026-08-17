package com.smartlearning.assistant.llmops;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LlmMetricsServiceTest {

    @Mock
    private LlmTraceRepository traceRepository;

    private LlmMetricsService metricsService;

    @BeforeEach
    void setUp() {
        metricsService = new LlmMetricsService(traceRepository);
    }

    @Test
    void shouldRecordTraceSuccessfully() {
        LlmTraceRecord record = new LlmTraceRecord(
                "t-1", 1L, "CHAT_RAG", "gemini-1.5-pro", 120L, 850L, 100L, 250L, "SUCCESS", null);
        when(traceRepository.save(any(LlmTraceRecord.class))).thenReturn(record);

        LlmTraceRecord saved = metricsService.recordTrace(record);

        assertNotNull(saved);
        assertEquals("t-1", saved.getTraceId());
        verify(traceRepository).save(record);
    }

    @Test
    void shouldComputeAggregatedMetrics() {
        LlmTraceRecord r1 = new LlmTraceRecord("t-1", 1L, "CHAT_RAG", "gemini-1.5-pro", 100L, 500L, 100L, 200L, "SUCCESS", null);
        LlmTraceRecord r2 = new LlmTraceRecord("t-2", 1L, "AGENTIC_CHAT", "gemini-1.5-pro", 200L, 1000L, 200L, 300L, "SUCCESS", null);
        when(traceRepository.findAll()).thenReturn(List.of(r1, r2));
        when(traceRepository.count()).thenReturn(2L);
        when(traceRepository.countByStatus("SUCCESS")).thenReturn(2L);

        LlmMetricsService.AggregatedMetrics metrics = metricsService.getAggregatedMetrics();

        assertNotNull(metrics);
        assertEquals(2L, metrics.totalCalls());
        assertEquals(100.0, metrics.successRate());
        assertEquals(750.0, metrics.avgTotalLatencyMs());
        assertEquals(800L, metrics.totalTokens());
    }
}
