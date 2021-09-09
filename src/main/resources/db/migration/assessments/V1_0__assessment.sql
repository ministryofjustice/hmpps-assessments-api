CREATE TABLE IF NOT EXISTS assessment
(
    assessment_id           SERIAL          PRIMARY KEY,
    assessment_uuid         UUID            UNIQUE,
    created_date            TIMESTAMP       NOT NULL,
    completed_date          TIMESTAMP
);