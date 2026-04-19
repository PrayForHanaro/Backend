DROP TABLE IF EXISTS Pension;
DROP TABLE IF EXISTS RecurringOffering;
DROP TABLE IF EXISTS Offering;

-- =============================================
-- 1. Offering (헌금 내역)
-- =============================================
CREATE TABLE Offering (
    `offeringId`    BIGINT         NOT NULL AUTO_INCREMENT,
    `userId`        BIGINT         NOT NULL,
    `orgId`         BIGINT         NOT NULL,
    `accountId`     BIGINT         NOT NULL,
    `offeringType`  VARCHAR(20)    NOT NULL,
    `amount`         DECIMAL(15, 2) NOT NULL,
    `offererName`   VARCHAR(50)    NULL,
    `prayerContent` VARCHAR(250)   NULL,
    `usedPoint`     DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `createdAt`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`offeringId`)
);

-- =============================================
-- 2. RecurringOffering
-- =============================================
CREATE TABLE RecurringOffering (
    `recurringId`      BIGINT         NOT NULL AUTO_INCREMENT,
    `userId`           BIGINT         NOT NULL,
    `accountId`        BIGINT         NOT NULL,
    `orgId`            BIGINT         NOT NULL,
    `offeringType`     VARCHAR(20)    NOT NULL,
    `amount`            DECIMAL(15, 2) NOT NULL,
    `startDate`        DATE           NOT NULL,
    `endDate`          DATE           NULL,
    `nextPaymentDate` DATE           NOT NULL,
    `isActive`         TINYINT(1)     NOT NULL DEFAULT 1,
    `createdAt`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`recurringId`)
);

-- =============================================
-- 3. Pension
-- =============================================
CREATE TABLE Pension (
    `pensionId`         BIGINT         NOT NULL AUTO_INCREMENT,
    `userId`            BIGINT         NOT NULL,
    `accountId`         BIGINT         NULL,
    `pensionType`       VARCHAR(20)    NOT NULL,
    `isHanaBank`       TINYINT(1)     NOT NULL DEFAULT 0,
    `totalContribution` DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `totalWithdrawal`   DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `profit`             DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `returnRate`        DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `institutionName`   VARCHAR(100)   NULL,
    `createdAt`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`         DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`pensionId`)
);

-- =============================================
-- SEED DATA
-- =============================================
-- 유저1의 헌금 릴레이
INSERT INTO Offering (`userId`, `orgId`, `accountId`, `offeringType`, `amount`, `offererName`, `prayerContent`, `usedPoint`) VALUES
(2, 2, 2, '십일조', 200000, '유저1', '4월의 첫 열매를 드립니다.', 0),
(2, 2, 2, '감사헌금', 50000, '유저1', '항상 지켜주심에 감사드립니다.', 5000),
(2, 2, 2, '감사헌금', 20000, '유저1', '작은 소망이 이루어져 감사합니다.', 0),
(2, 2, 2, '십일조', 200000, '유저1', '3월의 십일조를 드립니다.', 0),
(3, 1, 3, '선교헌금', 100000, '목사1', '땅끝까지 복음이 전해지길.', 0),
(1, 1, 1, '감사헌금', 1000000, '관리자1', '모든 영광을 주님께.', 0);

INSERT INTO RecurringOffering (`userId`, `accountId`, `orgId`, `offeringType`, `amount`, `startDate`, `nextPaymentDate`) VALUES
(2, 2, 2, '십일조', 200000, '2026-01-01', '2026-05-01'),
(3, 3, 1, '선교헌금', 50000, '2026-02-01', '2026-05-01');

INSERT INTO Pension (`userId`, `pensionType`, `isHanaBank`, `totalContribution`, `profit`, `returnRate`, `institutionName`) VALUES
(2, 'PERSONAL', 1, 10000000, 1500000, 15.0, '하나은행'),
(2, 'NATIONAL', 0, 5000000, 200000, 4.0, '국민연금공단'),
(3, 'RETIREMENT', 1, 15000000, 2000000, 13.3, '하나은행');
