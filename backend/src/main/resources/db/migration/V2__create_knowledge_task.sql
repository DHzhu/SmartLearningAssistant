CREATE TABLE knowledge_task (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    filename        VARCHAR(255) NOT NULL,
    s3_url          VARCHAR(512) NOT NULL,
    status          VARCHAR(32)  NOT NULL DEFAULT 'PENDING',
    error_message   TEXT,
    chunk_count     INTEGER      DEFAULT 0,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_knowledge_task_user_id ON knowledge_task (user_id);
CREATE INDEX idx_knowledge_task_status ON knowledge_task (status);
