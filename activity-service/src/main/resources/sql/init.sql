-- =============================================
-- 1. Activity (활동 게시글)
-- =============================================
CREATE TABLE IF NOT EXISTS Activity (
    `activityId`     BIGINT       NOT NULL AUTO_INCREMENT,
    `orgId`          BIGINT       NOT NULL,
    `creatorId`      BIGINT       NOT NULL,
    `activityCategory` VARCHAR(20) NOT NULL,
    `activityType`   VARCHAR(20)  NOT NULL,
    `activityState`  VARCHAR(20)  NOT NULL DEFAULT 'RECRUITING',
    `title`           VARCHAR(100) NOT NULL,
    `description`     TEXT         NULL,
    `location`        VARCHAR(255) NULL,
    `maxMembers`     INT          NOT NULL,
    `startAt`        DATETIME     NOT NULL,
    `endAt`          DATETIME     NOT NULL,
    `recurrence`      VARCHAR(20)  NULL,
    `pointAmount`    INT          NOT NULL DEFAULT 0,
    `createdAt`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`activityId`)
);

-- =============================================
-- 2. ActivityRecurrenceDay
-- =============================================
CREATE TABLE IF NOT EXISTS ActivityRecurrenceDay (
    `activityId`     BIGINT      NOT NULL,
    `recurrenceDay`  VARCHAR(20) NOT NULL,
    CONSTRAINT fk_recur_day_activity FOREIGN KEY (`activityId`) REFERENCES Activity (`activityId`)
);

-- =============================================
-- 3. ActivityRecurrenceMonthDay
-- =============================================
CREATE TABLE IF NOT EXISTS ActivityRecurrenceMonthDay (
    `activityId`           BIGINT NOT NULL,
    `recurrenceMonthDay`  INT    NOT NULL,
    CONSTRAINT fk_recur_month_activity FOREIGN KEY (`activityId`) REFERENCES Activity (`activityId`)
);

-- =============================================
-- 4. ActivityPhoto
-- =============================================
CREATE TABLE IF NOT EXISTS ActivityPhoto (
    `photoId`    BIGINT       NOT NULL AUTO_INCREMENT,
    `activityId` BIGINT       NOT NULL,
    `photoUrl`   VARCHAR(500) NOT NULL,
    `orderNum`   INT          NOT NULL,
    `createdAt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`photoId`),
    CONSTRAINT fk_photo_activity FOREIGN KEY (`activityId`) REFERENCES Activity (`activityId`)
);

-- =============================================
-- 5. ActivityApply
-- =============================================
CREATE TABLE IF NOT EXISTS ActivityApply (
    `applyId`     BIGINT   NOT NULL AUTO_INCREMENT,
    `activityId`  BIGINT   NOT NULL,
    `userId`      BIGINT   NOT NULL,
    `pointEarned` INT      NOT NULL DEFAULT 0,
    `createdAt`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`applyId`),
    CONSTRAINT uq_activity_apply UNIQUE (`activityId`, `userId`),
    CONSTRAINT fk_apply_activity FOREIGN KEY (`activityId`) REFERENCES Activity (`activityId`)
);

-- =============================================
-- 6. Calendar (캘린더 일정)
-- =============================================
CREATE TABLE IF NOT EXISTS Calendar (
    `calendarId`    BIGINT       NOT NULL AUTO_INCREMENT,
    `userId`        BIGINT       NOT NULL,
    `activityId`    BIGINT       NULL,
    `calendarType`  VARCHAR(10)  NOT NULL,
    `title`          VARCHAR(20)  NOT NULL,
    `startDateTime` DATETIME    NOT NULL,
    `endDateTime`   DATETIME    NOT NULL,
    `memo`           VARCHAR(100) NULL,
    `createdAt`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updatedAt`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`calendarId`),
    CONSTRAINT fk_calendar_activity FOREIGN KEY (`activityId`) REFERENCES Activity (`activityId`)
);

-- =============================================
-- SEED DATA (INSERT IGNORE 사용)
-- =============================================
INSERT IGNORE INTO Activity (`activityId`, `orgId`, `creatorId`, `activityCategory`, `activityType`, `activityState`, `title`, `description`, `location`, `maxMembers`, `startAt`, `endAt`, `recurrence`, `pointAmount`) VALUES
(1, 1, 3, 'VOLUNTEER', 'REGULAR', 'RECRUITING', '독거노인 도시락 배달 봉사', '매주 토요일 어르신들께 도시락을 배달합니다.', '하나교회 1층 식당', 10, '2026-05-02 09:00:00', '2026-12-26 12:00:00', 'WEEKLY', 100),
(2, 2, 3, 'FELLOWSHIP', 'REGULAR', 'RECRUITING', '주일 오후 배드민턴 소모임', '예배 후 즐겁게 운동해요!', '은혜교회 앞 체육관', 20, '2026-05-03 15:00:00', '2026-12-27 17:00:00', 'WEEKLY', 50),
(3, 1, 3, 'CHURCH_EVENT', 'ONCE', 'RECRUITING', '여름 성경 학교 교사 모집', '여름 성경 학교를 함께 준비해요.', '하나교회 중강당', 15, '2026-07-20 09:00:00', '2026-07-22 18:00:00', NULL, 200),
(4, 2, 3, 'VOLUNTEER', 'ONCE', 'CLOSED', '병원 안내 봉사', '인근 병원에서 환자분들을 돕습니다.', '하나성심병원', 5, '2026-04-10 10:00:00', '2026-04-10 14:00:00', NULL, 100),
(5, 1, 3, 'FELLOWSHIP', 'REGULAR', 'RECRUITING', '목요 시니어 찬양대', '목소리로 주님을 찬양하는 시니어 모임입니다.', '하나교회 소강당', 30, '2026-05-07 10:00:00', '2026-12-24 12:00:00', 'WEEKLY', 50);

INSERT IGNORE INTO ActivityRecurrenceDay (`activityId`, `recurrenceDay`) VALUES
(1, 'SATURDAY'), (2, 'SUNDAY'), (5, 'THURSDAY');

INSERT IGNORE INTO ActivityApply (`applyId`, `activityId`, `userId`, `pointEarned`) VALUES
(1, 1, 2, 100), (2, 2, 2, 50), (3, 4, 2, 100), (4, 5, 2, 50);

INSERT IGNORE INTO Calendar (`calendarId`, `userId`, `activityId`, `calendarType`, `title`, `startDateTime`, `endDateTime`, `memo`) VALUES
(1, 2, 1, 'CHURCH', '도시락 배달 봉사', '2026-05-02 09:00:00', '2026-05-02 12:00:00', '토요일 오전 봉사 필수!'),
(2, 2, 2, 'CHURCH', '배드민턴 모임', '2026-05-03 15:00:00', '2026-05-03 17:00:00', '라켓 챙기기'),
(3, 2, 5, 'CHURCH', '찬양대 연습', '2026-05-07 10:00:00', '2026-05-07 12:00:00', '가운 지참'),
(4, 2, NULL, 'PERSONAL', '아침 기도회', '2026-04-20 06:00:00', '2026-04-20 07:00:00', '새벽을 깨우는 기도'),
(5, 2, NULL, 'PERSONAL', '손주 돌보기', '2026-04-21 14:00:00', '2026-04-21 18:00:00', '맛있는 간식 사가기'),
(6, 2, NULL, 'PERSONAL', '성경 일독 도전', '2026-04-22 21:00:00', '2026-04-22 22:00:00', '시편 읽기 시작'),
(7, 2, NULL, 'PERSONAL', '건강검진 예약', '2026-04-25 10:00:00', '2026-04-25 11:00:00', '금식 확인'),
(8, 3, NULL, 'PERSONAL', '금요 철야 예배 인도', '2026-04-24 21:00:00', '2026-04-24 23:00:00', '설교 원고 준비'),
(9, 3, 3, 'CHURCH', '여름성경학교 기획', '2026-05-10 14:00:00', '2026-05-10 16:00:00', '예산 확인');
