CREATE TABLE IF NOT EXISTS ACTIVITY (
                                        activity_id              BIGINT       NOT NULL AUTO_INCREMENT,
                                        org_id                   BIGINT       NOT NULL COMMENT 'org_db 참조 FK없음',
                                        creator_id               BIGINT       NOT NULL COMMENT 'user_db 참조 FK없음',
                                        activity_category        VARCHAR(20)  NOT NULL COMMENT '봉사모집/동행찾기/교회행사',
    activity_type            VARCHAR(10)  NOT NULL COMMENT '일회성/정기',
    title                    VARCHAR(100) NOT NULL,
    description              TEXT         NULL,
    location                 VARCHAR(255) NULL,
    max_members              INT          NOT NULL,
    start_at                 DATETIME     NOT NULL,
    end_at                   DATETIME     NOT NULL,
    recurrence               VARCHAR(10)  NULL     COMMENT '정기일 경우에만: 매일/매주/매월',
    recurrence_day_of_month  INT          NULL     COMMENT '정기-매월인 경우 몇일',
    point_amount             INT          NOT NULL DEFAULT 0,
    created_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (activity_id)
    );

-- @ElementCollection → 별도 조인 테이블 자동 생성
-- 매주 반복 시 요일 목록 저장
CREATE TABLE IF NOT EXISTS ACTIVITY_RECURRENCE_DAYS (
                                                        activity_activity_id  BIGINT      NOT NULL,
                                                        recurrence_days       VARCHAR(10) NOT NULL COMMENT '월/화/수/목/금/토/일',
    CONSTRAINT fk_recurrence_activity
    FOREIGN KEY (activity_activity_id) REFERENCES ACTIVITY (activity_id)
    );

CREATE TABLE IF NOT EXISTS ACTIVITY_PHOTO (
                                              photo_id    BIGINT       NOT NULL AUTO_INCREMENT,
                                              activity_id BIGINT       NOT NULL,
                                              photo_url   VARCHAR(500) NOT NULL,
    order_num   TINYINT      NOT NULL COMMENT '1~3',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (photo_id),
    CONSTRAINT chk_order_num CHECK (order_num BETWEEN 1 AND 3),
    CONSTRAINT fk_photo_activity
    FOREIGN KEY (activity_id) REFERENCES ACTIVITY (activity_id)
    );

CREATE TABLE IF NOT EXISTS ACTIVITY_APPLY (
    apply_id     BIGINT   NOT NULL AUTO_INCREMENT,
    activity_id  BIGINT   NOT NULL,
    user_id      BIGINT   NOT NULL COMMENT 'user_db 참조 FK없음',
    point_earned INT      NOT NULL DEFAULT 0,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (apply_id),
    CONSTRAINT uq_activity_apply UNIQUE (activity_id, user_id),
    CONSTRAINT fk_apply_activity
    FOREIGN KEY (activity_id) REFERENCES ACTIVITY (activity_id)
    );

CREATE TABLE IF NOT EXISTS CALENDAR (
    calendar_id    BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL COMMENT 'user_db 참조 FK없음',
    activity_id    BIGINT       NULL,
    calendar_type  VARCHAR(10)  NOT NULL COMMENT '개인/교회',
    title          VARCHAR(20)  NOT NULL,
    start_date_time DATETIME    NOT NULL,
    end_date_time   DATETIME    NOT NULL,
    memo           VARCHAR(100) NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (calendar_id),
    CONSTRAINT fk_calendar_activity
    FOREIGN KEY (activity_id) REFERENCES ACTIVITY (activity_id)
    );