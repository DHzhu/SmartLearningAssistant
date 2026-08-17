# Track Specification: LLMOps & Observability

## 1. Goal
Introduce an end-to-end LLMOps tracing and telemetry engine to capture latency breakdowns, token consumption, and call traces across all RAG and Agentic interactions.

## 2. User Stories
- As a system administrator, I want to view LLM invocation metrics (average latency, token consumption, error rate) to monitor AI health and costs.
- As a developer, I want granular tracing on each request showing retrieval time vs generation time for performance tuning.

## 3. Technical Strategy
- **Telemetry & Tracing Entity**:
  - `LlmTraceRecord`: Stores `traceId`, `userId`, `actionType`, `modelName`, `retrievalLatencyMs`, `totalLatencyMs`, `promptTokens`, `completionTokens`, `status`.
  - Flyway migration `V5__create_llm_trace.sql` and `LlmTraceRepository`.
- **Metrics Service & Admin API**:
  - `LlmMetricsService`: Asynchronously captures span metrics and aggregates statistics.
  - `AdminMetricsController`: Protected `@PreAuthorize("hasRole('ADMIN')")` endpoint `/api/admin/metrics/llm`.
- **Key Dependencies**: Spring Data JPA, Spring Security.

## 4. Constraints & Standards
- Admin endpoints strictly protected by Spring Security RBAC.
- Metric recording must be non-blocking.
- Test coverage >80%.

## 5. Success Criteria
- [ ] `LlmTraceRecord` entity and repository implemented.
- [ ] `LlmMetricsService` correctly logs traces and calculates aggregated metrics.
- [ ] `AdminMetricsController` passes security tests and RBAC verification.
- [ ] Test suite passes with >80% coverage.

