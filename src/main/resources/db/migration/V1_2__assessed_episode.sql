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
    FOREIGN KEY (assessment_uuid)
        REFERENCES assessment(assessment_uuid)
);

-- noinspection SqlResolve,SqlResolve

CREATE TABLE IF NOT EXISTS question_answer
(
    question_answer_id          SERIAL      PRIMARY KEY,
    question_answer_uuid        UUID        UNIQUE,
    answer_code                 TEXT        NOT NULL,
    episode_uuid                UUID        NOT NULL,
    question_schema_uuid        UUID        NOT NULL,
    freetext_question_answer    TEXT,
    FOREIGN KEY (question_schema_uuid) REFERENCES question_schema(question_schema_uuid),
    FOREIGN KEY (episode_uuid) REFERENCES assessed_episode(episode_uuid)
);

-- noinspection SqlResolve,SqlResolve

CREATE TABLE IF NOT EXISTS answers
(
    question_answer_id      SERIAL      NOT NULL,
    question_answer_uuid    UUID        UNIQUE,
    answer_schema_uuid      UUID        NOT NULL,
    freetext_answer         TEXT,
    PRIMARY KEY (question_answer_uuid, answer_schema_uuid),
    FOREIGN KEY (answer_schema_uuid) REFERENCES answer_schema (answer_schema_uuid),
    FOREIGN KEY (question_answer_uuid) REFERENCES question_answer (question_answer_uuid)
);
