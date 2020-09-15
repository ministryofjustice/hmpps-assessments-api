CREATE TABLE IF NOT EXISTS offender
(
    offender_id     SERIAL      PRIMARY KEY,
    source_id       TEXT        NOT NULL,
    source          TEXT        NOT NULL,
    assessment_id   SERIAL      NOT NULL,
    FOREIGN KEY (assessment_id)
        REFERENCES assessment(assessment_id)
);