-- =============================================
-- 1. Gift
-- =============================================
CREATE TABLE IF NOT EXISTS Gift (
    `giftId`                INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    `senderId`              BIGINT        NOT NULL,
    `receiverId`            BIGINT        NULL,
    `giftReceiverType`     VARCHAR(20)   NOT NULL,
    `fromAccountId`        BIGINT        NOT NULL,
    `toSavingsAccountId`  BIGINT        NOT NULL,
    `amount`                 DECIMAL(15,2) NOT NULL,
    `isActive`              TINYINT(1)    NOT NULL DEFAULT 1,
    `cumulativeTotal`       DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `savingsProductName`   VARCHAR(100)  NOT NULL,
    `interestRate`          DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `createdAt`             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`giftId`)
);

-- =============================================
-- 2. PrayerSavings
-- =============================================
CREATE TABLE IF NOT EXISTS PrayerSavings (
    `prayerSavingsId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `giftId`              INT UNSIGNED NOT NULL,
    `prayerContent`    VARCHAR(100) NOT NULL,
    `startDate`        DATE         NOT NULL,
    `dDay`             INT          NOT NULL DEFAULT 0,
    `createdAt`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`prayerSavingsId`),
    CONSTRAINT fk_prayer_savings_gift FOREIGN KEY (`gift`) REFERENCES Gift (`giftId`) ON DELETE CASCADE
);

-- =============================================
-- SEED DATA (INSERT IGNORE 사용)
-- =============================================
INSERT IGNORE INTO Gift (`giftId`, `senderId`, `receiverId`, `giftReceiverType`, `fromAccountId`, `toSavingsAccountId`, `amount`, `cumulativeTotal`, `savingsProductName`, `interestRate`) VALUES
(1, 1, 2, 'CHILD', 1, 4, 100000, 200000, '하나 하나님의 적금', 3.5),
(2, 2, NULL, 'GRANDCHILD', 2, 999, 50000, 50000, '꿈나무 축복 적금', 4.0),
(3, 3, 2, 'CONGREGATION', 3, 4, 10000, 10000, '성도 사랑 적금', 2.5);

INSERT IGNORE INTO PrayerSavings (`prayerSavingsId`, `gift`, `prayerContent`, `startDate`, `dDay`) VALUES
(1, 1, '매달 쌓이는 적금처럼 당신을 향한 축복도 쌓여가길 기도합니다.', '2026-04-01', 19),
(2, 1, '지치고 힘들 때마다 이 적금이 작은 위로가 되길 바랍니다.', '2026-04-01', 19),
(3, 1, '오늘도 주님의 은혜 안에서 평안한 하루 보내세요.', '2026-04-01', 19),
(4, 2, '우리 아이가 하나님의 사랑 안에서 지혜롭게 자라기를 기도합니다.', '2026-04-10', 9),
(5, 2, '건강하게 자라주어 고맙다. 항상 응원한다.', '2026-04-10', 9),
(6, 3, '우리 성도님의 삶에 주님의 축복이 가득하길 소망합니다.', '2026-04-15', 4);
