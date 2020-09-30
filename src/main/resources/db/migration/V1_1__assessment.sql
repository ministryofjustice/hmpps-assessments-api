CREATE TABLE IF NOT EXISTS assessment
(
    assessment_id       SERIAL          PRIMARY KEY,
    assessment_uuid     UUID            UNIQUE,
    supervision_id      VARCHAR(50)     NOT NULL UNIQUE,
    created_date        TIMESTAMP       NOT NULL,
    completed_date      TIMESTAMP
);