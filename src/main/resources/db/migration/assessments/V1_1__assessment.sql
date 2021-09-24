CREATE TABLE IF NOT EXISTS assessment
(
    assessment_id           SERIAL          PRIMARY KEY,
    assessment_uuid         UUID            NOT NULL,
    subject_uuid            UUID        NOT NULL,
    created_date            TIMESTAMP       NOT NULL,
    completed_date          TIMESTAMP,
    FOREIGN KEY (subject_uuid) REFERENCES subject (subject_uuid)
);