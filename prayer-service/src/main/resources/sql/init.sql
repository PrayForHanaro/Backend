-- =============================================
-- 1. GIFT (증여 자동이체 설정)
-- 매달 지정된 사람의 적금계좌에 자동이체
-- 가입하지 않은 사용자에게 보낼 경우 receiver_id NULL
-- sender_id, receiver_id, from_account_id, to_savings_account_id
-- → user_db 참조, FK 없음
-- =============================================
CREATE TABLE IF NOT EXISTS GIFT (
    gift_id                BIGINT        NOT NULL AUTO_INCREMENT,
    sender_id              BIGINT        NOT NULL COMMENT '보내는 사람 ID, user_db 참조 FK없음',
    receiver_id            BIGINT        NULL     COMMENT '받는 사람 ID, 미가입자면 NULL, user_db 참조 FK없음',
    gift_receiver_type     VARCHAR(20)   NOT NULL COMMENT '수신자 유형',
    from_account_id        BIGINT        NOT NULL COMMENT '출금 계좌 ID, user_db 참조 FK없음',
    to_savings_account_id  BIGINT        NOT NULL COMMENT '입금 적금 계좌 ID, user_db 참조 FK없음',
    amount                 DECIMAL(15,2) NOT NULL COMMENT '매달 자동이체 금액',
    cumulative_total       DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '누적 송금 총액',
    is_active              TINYINT(1)    NOT NULL DEFAULT 1 COMMENT '1=활성화, 0=비활성화',
    savings_product_name   VARCHAR(100)  NOT NULL COMMENT '적금 상품 이름',
    interest_rate          DECIMAL(5,2)  NOT NULL DEFAULT 0 COMMENT '적금 상품 혜택률(%)',
    created_at             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (gift_id)
    );

-- =============================================
-- 2. PRAYER_SAVINGS (기도문 리스트)
-- Gift와 1:N 관계
-- 송금과 별개로 기도문만 관리
-- gift_id → 같은 DB, FK 정상 사용
-- =============================================
CREATE TABLE IF NOT EXISTS PRAYER_SAVINGS (
    prayer_savings_id BIGINT       NOT NULL AUTO_INCREMENT,
    gift_id           BIGINT       NOT NULL COMMENT '연결된 증여 ID',
    prayer_content    VARCHAR(100) NOT NULL COMMENT '기도문',
    start_date        DATE         NOT NULL COMMENT '기도 시작일',
    d_day             INT          NOT NULL DEFAULT 0 COMMENT 'D+N 현재 경과일수',
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (prayer_savings_id),
    CONSTRAINT fk_prayer_savings_gift
    FOREIGN KEY (gift_id) REFERENCES GIFT (gift_id)
    );