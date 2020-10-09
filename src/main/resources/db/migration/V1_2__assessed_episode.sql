-- noinspection SqlResolve,SqlResolve

CREATE TABLE IF NOT EXISTS assessed_episode
(
    episode_id          SERIAL          PRIMARY KEY,
    episode_uuid        UUID            UNIQUE,
    user_id             TEXT            NOT NULL,
    created_date        TIMESTAMP       NOT NULL,
    end_date            TIMESTAMP,
    change_reason       TEXT            NOT NULL,
    assessment_uuid     UUID            NOT NULL,
    answers             JSONB,
    FOREIGN KEY (assessment_uuid)
        REFERENCES assessment(assessment_uuid)
);