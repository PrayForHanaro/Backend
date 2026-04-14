CREATE TABLE IF NOT EXISTS OFFERING (
    offering_id    BIGINT        NOT NULL AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL,
    org_id         BIGINT        NOT NULL,
    account_id     BIGINT        NOT NULL,
    offering_type  VARCHAR(20)   NOT NULL,
    amount         DECIMAL(15,2) NOT NULL,
    is_named       TINYINT(1)    NOT NULL DEFAULT 1,
    offerer_name   VARCHAR(50)   NULL,
    prayer_content VARCHAR(250)  NULL,
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (offering_id)
    );

CREATE TABLE IF NOT EXISTS RECURRING_OFFERING (
    recurring_id      BIGINT        NOT NULL AUTO_INCREMENT,
    user_id           BIGINT        NOT NULL,
    account_id        BIGINT        NOT NULL,
    org_id            BIGINT        NOT NULL,
    offering_type     VARCHAR(20)   NOT NULL,
    amount            DECIMAL(15,2) NOT NULL,
    start_date        DATE          NOT NULL,
    end_date          DATE          NULL,
    next_payment_date DATE          NOT NULL,
    is_active         TINYINT(1)    NOT NULL DEFAULT 1,
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (recurring_id)
    );

CREATE TABLE IF NOT EXISTS PENSION (
    pension_id              BIGINT        NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT        NOT NULL,
    account_id              BIGINT        NULL,
    pension_type            VARCHAR(20)   NOT NULL,
    is_hana                 TINYINT(1)    NOT NULL DEFAULT 0,
    balance                 DECIMAL(15,2) NOT NULL DEFAULT 0,
    monthly_expected_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    start_date              DATE          NULL,
    expected_end_date       DATE          NULL,
    institution_name        VARCHAR(100)  NULL,
    created_at              DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pension_id)
    );

-- DONATION 수정 (user_id, offering_amount, donation_rate 추가)
CREATE TABLE IF NOT EXISTS DONATION (
    donation_id        BIGINT        NOT NULL AUTO_INCREMENT,
    org_id             BIGINT        NOT NULL,
    user_id            BIGINT        NOT NULL,
    offering_amount    DECIMAL(15,2) NOT NULL,
    donation_rate      DECIMAL(5,2)  NOT NULL,
    amount             DECIMAL(15,2) NOT NULL,
    hana_pension_count INT           NOT NULL DEFAULT 0,
    donated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (donation_id)
    );