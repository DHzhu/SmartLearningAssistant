package com.smartlearning.assistant.llmops;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/metrics")
public class AdminMetricsController {

    private final LlmMetricsService metricsService;

    public AdminMetricsController(LlmMetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/llm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LlmMetricsService.AggregatedMetrics> getLlmMetrics() {
        return ResponseEntity.ok(metricsService.getAggregatedMetrics());
    }
}
