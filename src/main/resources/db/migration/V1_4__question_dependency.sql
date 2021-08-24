-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS question_dependency
(
    dependency_id           SERIAL      PRIMARY KEY,
    subject_question_uuid   UUID        NOT NULL,
    trigger_question_uuid   UUID        NOT NULL,
    trigger_answer_value    TEXT        NOT NULL,
    dependency_start        TIMESTAMP   NOT NULL,
    dependency_end          TIMESTAMP,
    display_inline          BOOLEAN     NOT NULL,
    FOREIGN KEY (subject_question_uuid) REFERENCES question_schema (question_schema_uuid),
    FOREIGN KEY (trigger_question_uuid) REFERENCES question_schema (question_schema_uuid)

);