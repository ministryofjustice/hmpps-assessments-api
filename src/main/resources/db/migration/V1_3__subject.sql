-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS subject
(
    subject_id          SERIAL          PRIMARY KEY,
    subject_uuid        UUID            UNIQUE,
    source              TEXT            NOT NULL,
    source_id           TEXT            NOT NULL,
    name                TEXT            NOT NULL,
    oasys_offender_pk   NUMERIC         NULL,
    pnc                 TEXT            NULL,
    crn                 TEXT            NULL,
    date_of_birth       DATE            NOT NULL,
    created_date        TIMESTAMP       NOT NULL,
    assessment_uuid     UUID            NOT NULL,
    FOREIGN KEY (assessment_uuid)
        REFERENCES assessment(assessment_uuid)
);