-- =============================================
-- 1. OFFERING (헌금 내역)
-- 일회성 & 정기 헌금 모두 기록
-- user_id, org_id, account_id → 다른 DB 참조, FK 없음
-- =============================================
CREATE TABLE IF NOT EXISTS OFFERING (
    offering_id    BIGINT        NOT NULL AUTO_INCREMENT,
    user_id        BIGINT        NOT NULL COMMENT 'user_db 참조 FK없음',
    org_id         BIGINT        NOT NULL COMMENT 'org_db 참조 FK없음',
    account_id     BIGINT        NOT NULL COMMENT '출금 계좌 ID, user_db 참조 FK없음',
    offering_type  VARCHAR(20)   NOT NULL COMMENT '십일조/감사헌금/선교헌금/건축헌금/기타',
    amount         DECIMAL(15,2) NOT NULL,
    offerer_name   VARCHAR(50)   NULL     COMMENT '무기명이면 NULL',
    prayer_content VARCHAR(250)  NULL     COMMENT '기도제목 최대 250자',
    created_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (offering_id)
    );

-- =============================================
-- 2. RECURRING_OFFERING (정기 헌금 자동이체 설정)
-- 배치 스케줄러가 next_payment_date 기준으로 자동이체 실행
-- user_id, account_id, org_id → 다른 DB 참조, FK 없음
-- =============================================
CREATE TABLE IF NOT EXISTS RECURRING_OFFERING (
    recurring_id      BIGINT        NOT NULL AUTO_INCREMENT,
    user_id           BIGINT        NOT NULL COMMENT 'user_db 참조 FK없음',
    account_id        BIGINT        NOT NULL COMMENT '출금 계좌 ID, user_db 참조 FK없음',
    org_id            BIGINT        NOT NULL COMMENT 'org_db 참조 FK없음',
    offering_type     VARCHAR(20)   NOT NULL COMMENT '십일조/감사헌금/선교헌금/건축헌금/기타',
    amount            DECIMAL(15,2) NOT NULL,
    start_date        DATE          NOT NULL COMMENT '자동이체 시작일',
    end_date          DATE          NULL     COMMENT '자동이체 종료일, NULL이면 무기한',
    next_payment_date DATE          NOT NULL COMMENT '다음 납부 예정일, 이체 완료 후 다음달로 갱신',
    is_active         TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '1=진행중, 0=중지',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (recurring_id)
    );

-- =============================================
-- 3. PENSION (유저가 가입한 연금)
-- 하나은행 연금 등록 시 user.donation_rate 증가 (1개=1%)
-- user_id, account_id → 다른 DB 참조, FK 없음
-- =============================================
CREATE TABLE IF NOT EXISTS PENSION (
    pension_id         BIGINT        NOT NULL AUTO_INCREMENT,
    user_id            BIGINT        NOT NULL COMMENT 'user_db 참조 FK없음',
    account_id         BIGINT        NULL     COMMENT '연금 수령 계좌 ID, user_db 참조 FK없음',
    pension_type       VARCHAR(20)   NOT NULL COMMENT '국민연금/퇴직연금/개인연금',
    is_hana_bank       TINYINT(1)    NOT NULL DEFAULT 0 COMMENT '1=하나은행, 0=타행',
    total_contribution DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '총 납입액',
    total_withdrawal   DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '총 출금액',
    profit             DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '운용 수익',
    return_rate        DECIMAL(5,2)  NOT NULL DEFAULT 0 COMMENT '수익률(%)',
    institution_name   VARCHAR(100)  NULL     COMMENT '연금 기관명 예:하나은행/국민연금공단',
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (pension_id)
    );