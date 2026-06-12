CREATE TABLE sys_user_quota (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL UNIQUE,
    balance     BIGINT    NOT NULL DEFAULT 100000,
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sys_user_quota_user_id ON sys_user_quota (user_id);

CREATE TABLE sys_billing_log (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    amount      BIGINT       NOT NULL,
    type        VARCHAR(32)  NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sys_billing_log_user_id ON sys_billing_log (user_id);
CREATE INDEX idx_sys_billing_log_created_at ON sys_billing_log (created_at);
