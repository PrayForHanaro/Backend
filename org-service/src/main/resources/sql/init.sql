CREATE TABLE IF NOT EXISTS RELIGIOUS_ORG (
    org_id              BIGINT       NOT NULL AUTO_INCREMENT,
    org_type            VARCHAR(10)  NOT NULL,
    org_name            VARCHAR(100) NOT NULL,
    address             VARCHAR(255) NOT NULL,
    phone               VARCHAR(20)  NULL,
    representative_name VARCHAR(50)  NULL,
    registration_number VARCHAR(20)  NULL,
    member_count        INT          NOT NULL DEFAULT 0,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (org_id)
    );

CREATE TABLE IF NOT EXISTS DISTRICT (
    district_id    BIGINT      NOT NULL AUTO_INCREMENT,
    org_id         BIGINT      NOT NULL,
    district_name  VARCHAR(50) NOT NULL,
    district_order INT         NOT NULL,
    PRIMARY KEY (district_id),
    CONSTRAINT fk_district_org
    FOREIGN KEY (org_id) REFERENCES RELIGIOUS_ORG (org_id)
    );