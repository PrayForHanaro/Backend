CREATE TABLE IF NOT EXISTS AUTH_CREDENTIAL (
    credential_id BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL UNIQUE,                                              phone         VARCHAR(20)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(500) NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (credential_id)
    );