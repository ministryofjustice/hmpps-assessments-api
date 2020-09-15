CREATE TABLE IF NOT EXISTS assessment
(
    assessment_id   SERIAL      PRIMARY KEY,
    supervision_id  TEXT        NOT NULL,
    created_date    TIMESTAMP   NOT NULL,
    completed_date  TIMESTAMP
);