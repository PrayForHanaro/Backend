-- =============================================
-- 1. RELIGIOUS_ORG (종교단체)
-- 교회/성당/절 정보
-- representativeId → user_db 참조, FK 없음
-- accountId → 교회 계좌, API로 연결 FK 없음
-- =============================================
CREATE TABLE IF NOT EXISTS RELIGIOUS_ORG (
    religious_org_id      BIGINT        NOT NULL AUTO_INCREMENT,
    org_type              VARCHAR(10)   NOT NULL COMMENT '교회/성당/절',
    org_name              VARCHAR(100)  NOT NULL COMMENT '종교단체 이름',
    address               VARCHAR(30)   NOT NULL COMMENT '주소',
    phone                 VARCHAR(20)   NULL     COMMENT '대표 전화번호',
    representative_id     BIGINT        NULL     COMMENT '대표자 ID, user_db 참조 FK없음',
    account_id            BIGINT        NULL     COMMENT '교회 계좌 ID, API로 연결 FK없음',
    total_offering_amount DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '누적 헌금 총액',
    total_point_amount    DECIMAL(15,2) NOT NULL DEFAULT 0 COMMENT '누적 포인트 총액',
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (religious_org_id)
    );

-- =============================================
-- RELIGIOUS_ORG seed (테스트용)
-- =============================================
INSERT INTO RELIGIOUS_ORG
(org_type, org_name, address, phone, total_offering_amount, total_point_amount)
VALUES
    ('교회', '하나교회',     '서울시 성동구 왕십리로 1',    '02-1234-5678', 0, 0),
    ('교회', '은혜교회',     '서울시 강남구 테헤란로 2',    '02-2345-6789', 0, 0),
    ('교회', '사랑교회',     '서울시 서초구 반포대로 3',    '02-3456-7890', 0, 0),
    ('성당', '명동성당',     '서울시 중구 명동길 74',       '02-4567-8901', 0, 0),
    ('성당', '혜화동성당',   '서울시 종로구 혜화동 1',      '02-5678-9012', 0, 0),
    ('절',   '조계사',       '서울시 종로구 우정국로 55',   '02-6789-0123', 0, 0);