CREATE TABLE IF NOT EXISTS ACTIVITY (
    activity_id       BIGINT       NOT NULL AUTO_INCREMENT,
    org_id            BIGINT       NOT NULL,
    creator_id        BIGINT       NOT NULL,
    activity_category VARCHAR(20)  NOT NULL,
    activity_type     VARCHAR(10)  NOT NULL,
    title             VARCHAR(100) NOT NULL,
    description       TEXT         NULL,
    location          VARCHAR(255) NULL,
    max_members       INT          NOT NULL,
    start_at          DATETIME     NOT NULL,
    end_at            DATETIME     NULL,
    recurrence        VARCHAR(10)  NULL,
    recurrence_day    VARCHAR(20)  NULL,
    point_amount      INT          NOT NULL DEFAULT 0,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (activity_id)
    );

CREATE TABLE IF NOT EXISTS ACTIVITY_PHOTO (
    photo_id    BIGINT       NOT NULL AUTO_INCREMENT,
    activity_id BIGINT       NOT NULL,
    photo_url   VARCHAR(500) NOT NULL,
    order_num   TINYINT      NOT NULL,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id),
    CONSTRAINT chk_order_num CHECK (order_num BETWEEN 1 AND 3),
    CONSTRAINT fk_photo_activity
    FOREIGN KEY (activity_id) REFERENCES ACTIVITY (activity_id)
    );

CREATE TABLE IF NOT EXISTS ACTIVITY_APPLY (
    apply_id    BIGINT   NOT NULL AUTO_INCREMENT,
    activity_id BIGINT   NOT NULL,
    user_id     BIGINT   NOT NULL,
    point_earned INT     NOT NULL DEFAULT 0,
    applied_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (apply_id),
    CONSTRAINT uq_activity_apply UNIQUE (activity_id, user_id),
    CONSTRAINT fk_apply_activity
    FOREIGN KEY (activity_id) REFERENCES ACTIVITY (activity_id)
    );

CREATE TABLE IF NOT EXISTS POINT (
    point_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    apply_id   BIGINT       NOT NULL,
    amount     INT          NOT NULL,
    reason     VARCHAR(200) NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (point_id),
    CONSTRAINT fk_point_apply
    FOREIGN KEY (apply_id) REFERENCES ACTIVITY_APPLY (apply_id)
    );

CREATE TABLE IF NOT EXISTS CALENDAR (
    calendar_id   BIGINT       NOT NULL AUTO_INCREMENT,
    user_id       BIGINT       NOT NULL,
    activity_id   BIGINT       NULL,
    calendar_type VARCHAR(10)  NOT NULL,
    title         VARCHAR(100) NOT NULL,
    start_date    DATETIME     NOT NULL,
    end_date      DATETIME     NULL,
    memo          VARCHAR(500) NULL,
    created_at