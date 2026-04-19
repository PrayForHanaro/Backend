-- =============================================
-- 1. User (성도)
-- =============================================
CREATE TABLE IF NOT EXISTS User (
    `userId`           BIGINT        NOT NULL AUTO_INCREMENT,
    `orgId`            BIGINT        NULL     COMMENT '소속 교회 ID',
    `name`             VARCHAR(50)   NOT NULL COMMENT '성도 이름',
    `birthDate`        DATE          NOT NULL COMMENT '생년월일',
    `phone`            VARCHAR(20)   NOT NULL UNIQUE COMMENT '전화번호',
    `password`         VARCHAR(255)  NOT NULL COMMENT '비밀번호',
    `role`             VARCHAR(10)   NOT NULL COMMENT 'USER/CLERGY/ADMIN',
    `profileUrl`       VARCHAR(255)  NULL     COMMENT '프로필 URL',
    `defaultAccountId` BIGINT        NULL     COMMENT '기본 계좌 ID',
    `donationRate`     DOUBLE        NOT NULL DEFAULT 0,
    `pointSum`         INT           NOT NULL DEFAULT 0,
    `createdAt`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`userId`)
);

-- =============================================
-- 2. Account (계좌)
-- =============================================
CREATE TABLE IF NOT EXISTS Account (
    `accountId`     BIGINT         NOT NULL AUTO_INCREMENT,
    `userId`        BIGINT         NULL,
    `bankName`      VARCHAR(50)    NOT NULL,
    `accountNumber` VARCHAR(30)    NOT NULL UNIQUE,
    `balance`       DECIMAL(15, 2) NOT NULL DEFAULT 0,
    `isHana`        TINYINT(1)     NOT NULL DEFAULT 0,
    `isDefault`     TINYINT(1)     NOT NULL DEFAULT 0,
    `isSavings`     TINYINT(1)     NOT NULL DEFAULT 0,
    `version`       BIGINT         NOT NULL DEFAULT 0,
    `createdAt`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`accountId`),
    CONSTRAINT fk_account_user FOREIGN KEY (`userId`) REFERENCES User (`userId`)
);

ALTER TABLE User
    ADD CONSTRAINT fk_user_default_account FOREIGN KEY (`defaultAccountId`) REFERENCES Account (`accountId`);

-- =============================================
-- 3. Point (포인트 이력)
-- =============================================
CREATE TABLE IF NOT EXISTS Point (
    `pointId`   BIGINT       NOT NULL AUTO_INCREMENT,
    `userId`    BIGINT       NOT NULL,
    `pointType` VARCHAR(30)  NOT NULL,
    `amount`    INT          NOT NULL,
    `info`      VARCHAR(255) NULL,
    `createdAt` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`pointId`),
    CONSTRAINT fk_point_user FOREIGN KEY (`userId`) REFERENCES User (`userId`)
);

-- =============================================
-- 4. PointPolicy (포인트 정책)
-- =============================================
CREATE TABLE IF NOT EXISTS PointPolicy (
    `policyId`   BIGINT       NOT NULL AUTO_INCREMENT,
    `pointType`  VARCHAR(30)  NOT NULL UNIQUE,
    `amount`     INT          NOT NULL,
    `description` VARCHAR(100) NULL,
    `createdAt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`policyId`)
);

-- =============================================
-- SEED DATA (INSERT IGNORE 사용)
-- =============================================
INSERT IGNORE INTO User (`userId`, `orgId`, `name`, `birthDate`, `phone`, `password`, `role`, `donationRate`, `pointSum`) VALUES
(1, NULL, '관리자1', '1980-01-01', '010-0000-0000', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', 'ADMIN', 1.0, 5000),
(2, 2, '유저1', '1990-05-05', '010-1111-1111', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', 'USER', 2.0, 2450),
(3, 2, '목사1', '1970-12-12', '010-2222-2222', '$2a$10$u/K3oQJ0C2j9r6B.Z1QGz.e2g.6Gq5YpLqXpS6b6T6X6Y6Z6', 'CLERGY', 1.0, 800);

INSERT IGNORE INTO Account (`accountId`, `userId`, `bankName`, `accountNumber`, `balance`, `isHana`, `isDefault`, `isSavings`) VALUES
(1, 1, '하나은행', '111-111-111111', 1000000, 1, 1, 0),
(2, 2, '하나은행', '222-222-222222', 500000, 1, 1, 0),
(3, 3, '하나은행', '333-333-333333', 300000, 1, 1, 0),
(4, 2, '하나은행', '222-222-999999', 150000, 1, 0, 1),
(5, 3, '국민은행', '333-444-555555', 20000, 0, 0, 0);

UPDATE User SET `defaultAccountId` = 1 WHERE `userId` = 1 AND `defaultAccountId` IS NULL;
UPDATE User SET `defaultAccountId` = 2 WHERE `userId` = 2 AND `defaultAccountId` IS NULL;
UPDATE User SET `defaultAccountId` = 3 WHERE `userId` = 3 AND `defaultAccountId` IS NULL;

INSERT IGNORE INTO PointPolicy (`policyId`, `pointType`, `amount`, `description`) VALUES
(1, 'OFFERING_RECURRING', 500, '정기헌금 등록'),
(2, 'ACTIVITY_VOLUNTEER', 100, '봉사 활동 참여'),
(3, 'ACTIVITY_CHURCH', 50, '교회 행사 참여'),
(4, 'SAVINGS_JOIN', 5000, '정기 적금 가입');

INSERT IGNORE INTO Point (`pointId`, `userId`, `pointType`, `amount`, `info`) VALUES
(1, 2, 'OFFERING_RECURRING', 500, '정기헌금: 십일조 등록'),
(2, 2, 'ACTIVITY_VOLUNTEER', 100, '봉사활동: 독거노인 도시락 배달'),
(3, 2, 'ACTIVITY_VOLUNTEER', 100, '봉사활동: 병원 안내 봉사'),
(4, 2, 'SAVINGS_JOIN', 500, '적금가입: 하나 하나님의 적금'),
(5, 2, 'OFFERING_ONCE', 150, '헌금: 감사헌금 포인트 적립'),
(6, 2, 'ACTIVITY_CHURCH', 50, '교회활동: 배드민턴 소모임'),
(7, 2, 'OFFERING_ONCE', 1000, '이벤트: 하나은행 첫 헌금 감사 포인트'),
(8, 3, 'ACTIVITY_CHURCH', 50, '교회활동: 성경 공부 소모임'),
(9, 3, 'OFFERING_RECURRING', 500, '정기헌금: 선교헌금 등록'),
(10, 1, 'SAVINGS_JOIN', 5000, '관리자 이벤트 포인트');
