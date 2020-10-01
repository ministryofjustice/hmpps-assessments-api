-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS offender
(
    offender_id     VARCHAR(50)     PRIMARY KEY,
    offender_uuid   uuid            UNIQUE,
    source_id       TEXT            NOT NULL,
    source          TEXT            NOT NULL,
    assessment_uuid   UUID            NOT NULL,
    FOREIGN KEY (assessment_uuid)
        REFERENCES assessment(assessment_uuid)
);