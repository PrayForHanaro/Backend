CREATE TABLE IF NOT EXISTS USER (
    user_id            BIGINT      NOT NULL AUTO_INCREMENT,
    org_id             BIGINT      NOT NULL,
    district_id        BIGINT      NOT NULL,
    name               VARCHAR(50) NOT NULL,
    birth_date         DATE        NOT NULL,
    phone              VARCHAR(20) NOT NULL UNIQUE,
    role               VARCHAR(10) NOT NULL DEFAULT '일반',
    default_account_id BIGINT      NULL,
    last_checkin_at    DATETIME    NULL,
    created_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
    );

CREATE TABLE IF NOT EXISTS ACCOUNT (
    account_id     BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NOT NULL,
    bank_name      VARCHAR(50) NOT NULL,
    account_number VARCHAR(30) NOT NULL UNIQUE,
    is_hana        TINYINT(1)  NOT NULL DEFAULT 0,
    is_default     TINYINT(1)  NOT NULL DEFAULT 0,
    is_savings     TINYINT(1)  NOT NULL DEFAULT 0,
    created_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id),
    CONSTRAINT fk_account_user
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
    );

ALTER TABLE USER
    ADD CONSTRAINT fk_user_default_account
        FOREIGN KEY (default_account_id) REFERENCES ACCOUNT (account_id);

CREATE TABLE IF NOT EXISTS CHECKIN (
    checkin_id BIGINT   NOT NULL AUTO_INCREMENT,
    user_id    BIGINT   NOT NULL,
    checked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (checkin_id),
    CONSTRAINT fk_checkin_user
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
    );