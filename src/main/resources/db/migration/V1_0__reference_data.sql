CREATE TABLE IF NOT EXISTS question_schema
(
    question_schema_id      SERIAL      PRIMARY KEY,
    question_code           TEXT        NOT NULL,
    oasys_question_code     TEXT        NOT NULL,
    question_start          TIMESTAMP   NOT NULL,
    question_end            TIMESTAMP,
    answer_type             TEXT        NOT NULL,
    question_text           TEXT,
    question_help_text      TEXT
);

CREATE TABLE IF NOT EXISTS answer_schema
(
    answer_schema_id        SERIAL      PRIMARY KEY,
    answer_schema_code      TEXT        NOT NULL,
    question_schema_id      SERIAL      NOT NULL,
    answer_start            TIMESTAMP   NOT NULL,
    answer_end              TIMESTAMP,
    value                   TEXT        NOT NULL,
    text                    TEXT        NOT NULL,
    FOREIGN KEY (question_schema_id) REFERENCES question_schema (question_schema_id)
);


CREATE TABLE IF NOT EXISTS question_answer
(
    question_answer_id          SERIAL      PRIMARY KEY,
    answer_code                 TEXT        NOT NULL,
    episode_id                  SERIAL      NOT NULL,
    question_schema_id          SERIAL      NOT NULL,
    freetext_question_answer    TEXT,
    FOREIGN KEY (question_schema_id) REFERENCES question_schema(question_schema_id)
);

CREATE TABLE IF NOT EXISTS answers
(
    question_answer_id  SERIAL      NOT NULL,
    answer_schema_id    SERIAL      NOT NULL,
    freetext_answer     TEXT,
    PRIMARY KEY (question_answer_id, answer_schema_id),
    FOREIGN KEY (answer_schema_id) REFERENCES answer_schema (answer_schema_id),
    FOREIGN KEY (question_answer_id) REFERENCES question_answer (question_answer_id)
);




CREATE TABLE IF NOT EXISTS "group"
(
    group_id    SERIAL  PRIMARY KEY,
    heading     TEXT    NOT NULL,
    subheading  TEXT,
    help_text   TEXT
);

CREATE TABLE IF NOT EXISTS question_group
(
    question_id         SERIAL      NOT NULL,
    group_id            SERIAL      NOT NULL,
    group_name          TEXT        NOT NULL,
    parent_group_id     SERIAL,
    display_order       TEXT      NOT NULL,
    mandatory           TEXT        NOT NULL,
    validation          TEXT,
    group_start         TIMESTAMP   NOT NULL,
    group_end           TIMESTAMP,
    PRIMARY KEY (question_id, group_id),
    FOREIGN KEY (question_id) REFERENCES question_schema(question_schema_id),
    FOREIGN KEY (group_id) REFERENCES "group"(group_id)
);