-- =============================================
-- 1. SavingsProduct (적금 상품 마스터, 활성 1건)
-- =============================================
CREATE TABLE IF NOT EXISTS SAVINGS_PRODUCT (
    `savingsProductId` BIGINT        NOT NULL AUTO_INCREMENT,
    `name`             VARCHAR(100)  NOT NULL,
    `interestRate`     DECIMAL(5,3)  NOT NULL,
    `isActive`         TINYINT(1)    NOT NULL DEFAULT 0,
    `createdAt`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`savingsProductId`)
);

-- =============================================
-- 2. Gift
-- =============================================
CREATE TABLE IF NOT EXISTS Gift (
    `giftId`                INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    `senderId`              BIGINT        NOT NULL,
    `receiverId`            BIGINT        NOT NULL,
    `giftReceiverType`      VARCHAR(20)   NOT NULL,
    `fromAccountId`         BIGINT        NOT NULL,
    `toSavingsAccountId`    BIGINT        NOT NULL,
    `amount`                DECIMAL(15,2) NOT NULL,
    `transferDay`           INT           NOT NULL,
    `goalDays`              INT           NOT NULL,
    `isActive`              TINYINT(1)    NOT NULL DEFAULT 1,
    `cumulativeTotal`       DECIMAL(15,2) NOT NULL DEFAULT 0,
    `savingsProductId`      BIGINT        NOT NULL,
    `savingsProductName`    VARCHAR(100)  NOT NULL,
    `interestRate`          DECIMAL(15,2) NOT NULL DEFAULT 0,
    `createdAt`             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`             DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`giftId`),
    CONSTRAINT uq_gift_sender_receiver UNIQUE (`senderId`, `receiverId`)
);

-- =============================================
-- 3. PrayerSavings
-- =============================================
CREATE TABLE IF NOT EXISTS PrayerSavings (
    `prayerSavingsId` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `giftId`          INT UNSIGNED NOT NULL,
    `prayerContent`   VARCHAR(250) NOT NULL,
    `startDate`       DATE         NOT NULL,
    `createdAt`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`prayerSavingsId`),
    CONSTRAINT fk_prayer_savings_gift FOREIGN KEY (`giftId`) REFERENCES Gift (`giftId`) ON DELETE CASCADE
);

-- =============================================
-- 4. GiftTransfer (정기이체 이력. Gift N:1)
-- =============================================
CREATE TABLE IF NOT EXISTS GIFT_TRANSFER (
    `transferId`     BIGINT        NOT NULL AUTO_INCREMENT,
    `gift_id`        INT UNSIGNED  NOT NULL,
    `transferDate`   DATE          NOT NULL,
    `amount`         DECIMAL(15,2) NOT NULL,
    `status`         VARCHAR(10)   NOT NULL,
    `failureReason`  VARCHAR(255)  NULL,
    `createdAt`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`transferId`),
    CONSTRAINT fk_gift_transfer_gift FOREIGN KEY (`gift_id`) REFERENCES Gift (`giftId`),
    INDEX idx_gift_transfer_gift_date (`gift_id`, `transferDate` DESC)
);

-- =============================================
-- 5. OnceTransfer (일회성 축복 송금. Gift와 독립)
-- =============================================
CREATE TABLE IF NOT EXISTS ONCE_TRANSFER (
    `onceTransferId`    BIGINT        NOT NULL AUTO_INCREMENT,
    `sender_id`         BIGINT        NOT NULL,
    `fromAccountId`     BIGINT        NOT NULL,
    `toAccountNumber`   VARCHAR(50)   NOT NULL,
    `amount`            DECIMAL(15,2) NOT NULL,
    `message`           VARCHAR(250)  NULL,
    `sentAt`            DATETIME(6)   NOT NULL,
    `createdAt`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`onceTransferId`),
    INDEX idx_once_transfer_sender_sent (`sender_id`, `sentAt` DESC)
);

-- =============================================
-- SEED DATA (INSERT IGNORE 사용)
-- =============================================
INSERT IGNORE INTO SAVINGS_PRODUCT (`savingsProductId`, `name`, `interestRate`, `isActive`) VALUES
(1, '하나 사랑적금', 5.000, 1);

INSERT IGNORE INTO Gift (`giftId`, `senderId`, `receiverId`, `giftReceiverType`, `fromAccountId`, `toSavingsAccountId`, `amount`, `transferDay`, `goalDays`, `cumulativeTotal`, `savingsProductId`, `savingsProductName`, `interestRate`) VALUES
(1, 1, 2, 'GRANDCHILD', 1, 4, 100000, 15, 365, 200000, 1, '하나 하나님의 적금', 3.5),
(2, 2, 3, 'GRANDCHILD', 2, 999, 50000, 10, 365, 50000, 1, '꿈나무 축복 적금', 4.0),
(3, 3, 2, 'GRANDCHILD', 3, 4, 10000, 5, 180, 10000, 1, '성도 사랑 적금', 2.5);

INSERT IGNORE INTO PrayerSavings (`prayerSavingsId`, `giftId`, `prayerContent`, `startDate`) VALUES
(1, 1, '매달 쌓이는 적금처럼 당신을 향한 축복도 쌓여가길 기도합니다.', '2026-04-01'),
(2, 1, '지치고 힘들 때마다 이 적금이 작은 위로가 되길 바랍니다.', '2026-04-01'),
(3, 1, '오늘도 주님의 은혜 안에서 평안한 하루 보내세요.', '2026-04-01'),
(4, 2, '우리 아이가 하나님의 사랑 안에서 지혜롭게 자라기를 기도합니다.', '2026-04-10'),
(5, 2, '건강하게 자라주어 고맙다. 항상 응원한다.', '2026-04-10'),
(6, 3, '우리 성도님의 삶에 주님의 축복이 가득하길 소망합니다.', '2026-04-15');

-- GiftTransfer: SUCCESS 합계가 Gift.cumulativeTotal과 일치하도록 시드
-- Gift1 (cumulative 200000, amount 100000) → 100k×2회
-- Gift2 (cumulative 50000,  amount 50000)  → 50k×1회
-- Gift3 (cumulative 10000,  amount 10000)  → 10k×1회
INSERT IGNORE INTO GIFT_TRANSFER (`transferId`, `gift_id`, `transferDate`, `amount`, `status`, `failureReason`) VALUES
(1, 1, '2026-03-15', 100000, 'SUCCESS', NULL),
(2, 1, '2026-04-15', 100000, 'SUCCESS', NULL),
(3, 2, '2026-04-10',  50000, 'SUCCESS', NULL),
(4, 3, '2026-04-05',  10000, 'SUCCESS', NULL);

-- OnceTransfer: 메시지 있는 케이스 1건 + 없는 케이스 1건
INSERT IGNORE INTO ONCE_TRANSFER (`onceTransferId`, `sender_id`, `fromAccountId`, `toAccountNumber`, `amount`, `message`, `sentAt`) VALUES
(1, 1, 1, '111-222-333333', 50000, '응원해, 항상 건강하길 기도해.', '2026-04-18 10:30:00.000000'),
(2, 2, 2, '444-555-666666', 30000, NULL,                              '2026-04-19 09:15:00.000000');
