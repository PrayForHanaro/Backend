-- =============================================
-- 1. ReligiousOrg (종교단체)
-- =============================================
DROP TABLE IF EXISTS ReligiousOrg;

CREATE TABLE IF NOT EXISTS ReligiousOrg (
    `religiousOrgId`      BIGINT         NOT NULL AUTO_INCREMENT,
    `orgType`              VARCHAR(10)    NOT NULL,
    `orgName`              VARCHAR(100)   NOT NULL,
    `address`               VARCHAR(100)   NOT NULL,
    `phone`                 VARCHAR(20)    NULL,
    `representativeId`     BIGINT         NULL,
    `accountId`            BIGINT         NOT NULL,
    `totalOfferingAmount` DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `totalPointAmount`    DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `createdAt`            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`            DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`religiousOrgId`)
);

-- =============================================
-- SEED DATA
-- =============================================
INSERT INTO ReligiousOrg (`religiousOrgId`, `orgType`, `orgName`, `address`, `phone`, `representativeId`, `accountId`) VALUES
(1, 'CHURCH', '하나교회', '서울시 성동구 왕십리로 1', '02-1234-5678', 3, 100),
(2, 'CHURCH', '은혜교회', '서울시 강남구 테헤란로 2', '02-2345-6789', NULL, 101),
(3, 'CATHOLIC', '명동성당', '서울시 중구 명동길 74', '02-3456-7890', NULL, 102),
(4, 'TEMPLE', '조계사', '서울시 종로구 우정국로 55', '02-4567-8901', NULL, 103),
(5, 'CHURCH', '사랑교회', '서울시 서초구 반포대로 3', '02-5555-5555', NULL, 104);
