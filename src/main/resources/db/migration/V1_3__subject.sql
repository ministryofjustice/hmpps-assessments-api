-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS subject
(
    subject_id      VARCHAR(50)     PRIMARY KEY,
    subject_uuid    uuid            UNIQUE,
    source          TEXT            NOT NULL,
    source_id       TEXT            NOT NULL,
    name            TEXT            NOT NULL,
    pnc             TEXT            NULL,
    crn             TEXT            NULL,
    date_of_birth   DATE            NULL,
    created_date    TIMESTAMP       NOT NULL,
    assessment_uuid UUID            NOT NULL,
    FOREIGN KEY (assessment_uuid)
        REFERENCES assessment(assessment_uuid)
);