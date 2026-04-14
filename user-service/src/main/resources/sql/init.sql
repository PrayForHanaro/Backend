CREATE TABLE IF NOT EXISTS USER (
    user_id            BIGINT        NOT NULL AUTO_INCREMENT,
    org_id             BIGINT        NOT NULL,
    district_id        BIGINT        NOT NULL,
    name               VARCHAR(50)   NOT NULL,
    birth_date         DATE          NOT NULL,
    phone              VARCHAR(20)   NOT NULL UNIQUE,
    role               VARCHAR(10)   NOT NULL DEFAULT '일반',
    default_account_id BIGINT        NULL,
    last_checkin_at    DATETIME      NULL,
    donation_rate      DECIMAL(5,2)  NOT NULL DEFAULT 0,  -- ← 헌금률 추가
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

-- =============================================
-- POINT (user_db로 이동)
-- =============================================
CREATE TABLE IF NOT EXISTS POINT (
    point_id    BIGINT       NOT NULL AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    point_type  VARCHAR(20)  NOT NULL,   -- 지급 유형
    amount      INT          NOT NULL,   -- 지급량 (음수면 차감)
    reason      VARCHAR(200) NULL,       -- 지급 사유
    ref_id      BIGINT       NULL,       -- 관련 ID (offering_id, apply_id 등)
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (point_id),
    CONSTRAINT fk_point_user
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
    );

-- =============================================
-- POINT_POLICY (포인트 정책 - 고정값 관리)
-- =============================================
CREATE TABLE IF NOT EXISTS POINT_POLICY (
    policy_id   BIGINT       NOT NULL AUTO_INCREMENT,
    point_type  VARCHAR(20)  NOT NULL UNIQUE,  -- 지급 유형
    amount      INT          NOT NULL,          -- 고정 지급량
    description VARCHAR(100) NULL,
    PRIMARY KEY (policy_id)
    );

-- 기본 정책 데이터 삽입 (포인트 지급량은 수정 가능)
INSERT INTO POINT_POLICY (point_type, amount, description) VALUES
    ('ACTIVITY_VOLUNTEER',  100,  '봉사 활동 참여'),
    ('ACTIVITY_CHURCH',      50,  '교회 행사 참여'),
    ('OFFERING_RECURRING',  500,  '정기 헌금 등록'),
    ('SAVINGS_JOIN',       5000,  '정기 적금 가입'),
    ('SAVINGS_AUTO',         10,  '자동 이체 실행');
-- 일시헌금은 헌금액 × donation_rate 계산이라 정책 없음