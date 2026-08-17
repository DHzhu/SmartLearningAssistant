CREATE TABLE sys_user_memory (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    category    VARCHAR(32)  NOT NULL,
    content     TEXT         NOT NULL,
    importance  INT          NOT NULL DEFAULT 3,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sys_user_memory_user_id ON sys_user_memory (user_id);
CREATE INDEX idx_sys_user_memory_category ON sys_user_memory (category);
