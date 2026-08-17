CREATE TABLE sys_llm_trace (
    id                    BIGSERIAL PRIMARY KEY,
    trace_id              VARCHAR(64)  NOT NULL,
    user_id               BIGINT       NOT NULL,
    action_type           VARCHAR(32)  NOT NULL,
    model_name            VARCHAR(64)  NOT NULL,
    retrieval_latency_ms  BIGINT       NOT NULL DEFAULT 0,
    total_latency_ms      BIGINT       NOT NULL DEFAULT 0,
    prompt_tokens         BIGINT       NOT NULL DEFAULT 0,
    completion_tokens     BIGINT       NOT NULL DEFAULT 0,
    status                VARCHAR(32)  NOT NULL DEFAULT 'SUCCESS',
    error_message         VARCHAR(500),
    created_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sys_llm_trace_trace_id ON sys_llm_trace (trace_id);
CREATE INDEX idx_sys_llm_trace_user_id ON sys_llm_trace (user_id);
CREATE INDEX idx_sys_llm_trace_created_at ON sys_llm_trace (created_at);
