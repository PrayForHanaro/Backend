CREATE TABLE IF NOT EXISTS TRANSFER (
    transfer_id             BIGINT        NOT NULL AUTO_INCREMENT,
    sender_id               BIGINT        NOT NULL,
    receiver_account_number VARCHAR(30)   NOT NULL,
    amount                  DECIMAL(15,2) NOT NULL,
    prayer_message          VARCHAR(500)  NULL,
    kakao_sent              TINYINT(1)    NOT NULL DEFAULT 0,
    created_at              DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (transfer_id)
    );

CREATE TABLE IF NOT EXISTS GIFT (
    gift_id               BIGINT        NOT NULL AUTO_INCREMENT,
    sender_id             BIGINT        NOT NULL,
    receiver_id           BIGINT        NOT NULL,
    from_account_id       BIGINT        NOT NULL,
    to_savings_account_id BIGINT        NOT NULL,
    amount                DECIMAL(15,2) NOT NULL,
    is_active             TINYINT(1)    NOT NULL DEFAULT 1,
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (gift_id)
    );

CREATE TABLE IF NOT EXISTS GIFT_LOG (
    log_id           BIGINT        NOT NULL AUTO_INCREMENT,
    gift_id          BIGINT        NOT NULL,
    sender_id        BIGINT        NOT NULL,
    receiver_id      BIGINT        NOT NULL,
    amount           DECIMAL(15,2) NOT NULL,
    message          VARCHAR(500)  NULL,
    cumulative_total DECIMAL(15,2) NOT NULL DEFAULT 0,
    transferred_at   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (log_id),
    CONSTRAINT fk_gift_log_gift
    FOREIGN KEY (gift_id) REFERENCES GIFT (gift_id)
    );

-- ⚠️ PRAYER_SAVINGS 추가
CREATE TABLE IF NOT EXISTS PRAYER_SAVINGS (
    prayer_savings_id     BIGINT        NOT NULL AUTO_INCREMENT,
    user_id               BIGINT        NOT NULL,
    to_savings_account_id BIGINT        NOT NULL,
    prayer_title          VARCHAR(100)  NOT NULL,
    prayer_target         VARCHAR(50)   NULL,
    target_amount         DECIMAL(15,2) NOT NULL,
    monthly_amount        DECIMAL(15,2) NOT NULL,
    current_amount        DECIMAL(15,2) NOT NULL DEFAULT 0,
    start_date            DATE          NOT NULL,
    end_date              DATE          NULL,
    d_day                 INT           NOT NULL DEFAULT 0,
    total_days            INT           NOT NULL DEFAULT 100,
    is_active             TINYINT(1)    NOT NULL DEFAULT 1,
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (prayer_savings_id)
    );