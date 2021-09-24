CREATE TABLE IF NOT EXISTS assessment
(
    assessment_id           SERIAL          PRIMARY KEY,
    assessment_uuid         UUID            UNIQUE,
    subject_uuid            UUID,
    created_date            TIMESTAMP       NOT NULL,
    completed_date          TIMESTAMP,
    FOREIGN KEY (subject_uuid) REFERENCES subject (subject_uuid)
);