-- =============================================
-- 1. USER (성도)
-- org_id → org_db 참조, FK 없음
-- default_account_id → 순환참조로 인해 ALTER TABLE로 후처리
-- =============================================
CREATE TABLE IF NOT EXISTS USER (
    user_id            BIGINT        NOT NULL AUTO_INCREMENT,
    org_id             BIGINT        NOT NULL COMMENT '소속 교회 ID, org_db 참조 FK없음',
    district_id        BIGINT        NULL     COMMENT '소속 구역 ID, org_db 참조 FK없음',
    name               VARCHAR(50)   NOT NULL COMMENT '성도 이름',
    birth_date         DATE          NOT NULL COMMENT '생년월일',
    phone              VARCHAR(20)   NOT NULL UNIQUE COMMENT '전화번호, 로그인 ID로 사용 형식:010-0000-0000',
    role               VARCHAR(20)   NOT NULL COMMENT '일반사용자/교직자/관리자',
    default_account_id BIGINT        NULL     COMMENT '기본 출금 계좌 ID, 순환참조로 후처리',
    donation_rate      DOUBLE        NOT NULL DEFAULT 0 COMMENT '헌금 기여율, 하나은행 연금 1개당 1% 증가',
    image_type         VARCHAR(20)   DEFAULT 'man' COMMENT '프로필 이미지 타입: man/woman/baby',
    created_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
    );

-- =============================================
-- 2. ACCOUNT (계좌)
-- user_id → 같은 DB, FK 정상 사용
-- 자녀 계좌일 경우 user_id NULL 허용
-- =============================================
CREATE TABLE IF NOT EXISTS ACCOUNT (
    account_id     BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NULL     COMMENT '계좌 명의 주인, 자녀 계좌면 NULL',
    bank_name      VARCHAR(50) NOT NULL COMMENT '은행명 예:하나은행/국민은행',
    account_number VARCHAR(30) NOT NULL UNIQUE COMMENT '계좌번호',
    is_hana        TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '1=하나은행, 0=타행',
    is_default     TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '1=기본 출금 계좌',
    is_savings     TINYINT(1)  NOT NULL DEFAULT 0 COMMENT '1=적금 계좌, 기도적금/증여 입금 대상',
    created_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id),
    CONSTRAINT fk_account_user
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
    );

-- USER.default_account_id FK 후처리
-- ACCOUNT 테이블 생성 후 실행
ALTER TABLE USER
    ADD CONSTRAINT fk_user_default_account
        FOREIGN KEY (default_account_id) REFERENCES ACCOUNT (account_id);

-- =============================================
-- 3. POINT (포인트 적립/차감 이력)
-- 모든 서비스의 포인트 지급은 user-service API 호출로 통일
-- refId → 다른 서비스 ID 참조, FK 없음
-- =============================================
CREATE TABLE IF NOT EXISTS POINT (
    point_id   BIGINT      NOT NULL AUTO_INCREMENT,
    user_id    BIGINT      NOT NULL COMMENT '포인트 대상 성도',
    point_type VARCHAR(30) NOT NULL COMMENT 'OFFERING_ONCE/OFFERING_RECURRING/ACTIVITY_VOLUNTEER/ACTIVITY_CHURCH/SAVINGS_JOIN',
    amount     INT         NOT NULL COMMENT '양수=적립, 음수=차감',
    ref_id     BIGINT      NULL     COMMENT '출처 ID, 다른 서비스 ID FK없음',
    created_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (point_id),
    CONSTRAINT fk_point_user
    FOREIGN KEY (user_id) REFERENCES USER (user_id)
    );

-- =============================================
-- 4. POINT_POLICY (포인트 지급 정책)
-- 고정 포인트 정책 관리
-- 일시헌금은 헌금액 × donation_rate 계산이라 정책 없음
-- =============================================
CREATE TABLE IF NOT EXISTS POINT_POLICY (
    policy_id   BIGINT      NOT NULL AUTO_INCREMENT,
    point_type  VARCHAR(30) NOT NULL UNIQUE COMMENT '지급 유형',
    amount      INT         NOT NULL COMMENT '고정 지급 포인트',
    description VARCHAR(100) NULL    COMMENT '정책 설명',
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (policy_id)
    );


-- =============================================
-- USER seed (테스트용)
-- =============================================
INSERT INTO USER
(org_id, name, birth_date, phone, password, role, donation_rate, image_type)
VALUES
    -- 하나교회 (org_id = 1)
    (1, '홍길동', '1960-03-15', '010-1111-1111', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '교직자',      1.0, 'man'),
    (1, '김영희', '1965-07-22', '010-2222-2222', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '관리자',      2.0, 'woman'),
    (1, '이철수', '1970-11-05', '010-3333-3333', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '일반사용자', 0.0, 'man'),
    (1, '박순자', '1958-01-30', '010-4444-4444', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '일반사용자',      1.0, 'woman'),
    (1, '최민준', '1975-09-18', '010-5555-5555', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '일반사용자', 0.0, 'baby'),

    -- 은혜교회 (org_id = 2)
    (2, '정미영', '1963-05-12', '010-6666-6666', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '교직자',      3.0, 'woman'),
    (2, '강동원', '1980-02-28', '010-7777-7777', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '일반사용자', 0.0, 'man'),

    -- 명동성당 (org_id = 4)
    (4, '윤서연', '1968-08-14', '010-8888-8888', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '일반사용자', 0.0, 'woman'),
    (4, '임재현', '1972-12-03', '010-9999-9999', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', '교직자',      1.0, 'man');

-- =============================================
-- ACCOUNT seed (테스트용)
-- =============================================
INSERT INTO ACCOUNT
(user_id, bank_name, account_number, is_hana, is_default, is_savings)
VALUES
    -- 홍길동 (user_id = 1)
    (1, '하나은행', '111-222-333333', 1, 1, 0),
    (1, '하나은행', '111-222-444444', 1, 0, 1),

    -- 김영희 (user_id = 2)
    (2, '하나은행', '222-333-111111', 1, 1, 0),
    (2, '국민은행', '222-333-222222', 0, 0, 1),

    -- 이철수 (user_id = 3)
    (3, '하나은행', '333-444-111111', 1, 1, 0),

    -- 박순자 (user_id = 4)
    (4, '신한은행', '444-555-111111', 0, 1, 0),
    (4, '하나은행', '444-555-222222', 1, 0, 1),

    -- 최민준 (user_id = 5)
    (5, '하나은행', '555-666-111111', 1, 1, 0),

    -- 정미영 (user_id = 6)
    (6, '하나은행', '666-777-111111', 1, 1, 0),
    (6, '하나은행', '666-777-222222', 1, 0, 1),

    -- 강동원 (user_id = 7)
    (7, '우리은행', '777-888-111111', 0, 1, 0),

    -- 윤서연 (user_id = 8)
    (8, '하나은행', '888-999-111111', 1, 1, 0),

    -- 임재현 (user_id = 9)
    (9, '하나은행', '999-000-111111', 1, 1, 0),
    (9, '하나은행', '999-000-222222', 1, 0, 1);

-- USER default_account_id 업데이트
UPDATE USER SET default_account_id = 1  WHERE user_id = 1;
UPDATE USER SET default_account_id = 3  WHERE user_id = 2;
UPDATE USER SET default_account_id = 5  WHERE user_id = 3;
UPDATE USER SET default_account_id = 6  WHERE user_id = 4;
UPDATE USER SET default_account_id = 8  WHERE user_id = 5;
UPDATE USER SET default_account_id = 9  WHERE user_id = 6;
UPDATE USER SET default_account_id = 11 WHERE user_id = 7;
UPDATE USER SET default_account_id = 12 WHERE user_id = 8;
UPDATE USER SET default_account_id = 13 WHERE user_id = 9;


-- =============================================
-- POINT_POLICY 기본 데이터 (seed)
-- =============================================
INSERT INTO POINT_POLICY (point_type, amount, description) VALUES
                                                               ('OFFERING_RECURRING',  500,  '정기헌금 등록'),
                                                               ('ACTIVITY_VOLUNTEER',  100,  '봉사 활동 참여'),
                                                               ('ACTIVITY_CHURCH',      50,  '교회 행사 참여'),
                                                               ('SAVINGS_JOIN',       5000,  '정기 적금 가입');
