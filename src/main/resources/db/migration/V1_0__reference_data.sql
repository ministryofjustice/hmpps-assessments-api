CREATE TABLE IF NOT EXISTS question_schema
(
    question_schema_id      SERIAL      PRIMARY KEY,
    question_schema_uuid    UUID        NOT NULL unique,
    question_code           TEXT        NOT NULL,
    oasys_question_code     TEXT,
    question_start          TIMESTAMP   NOT NULL,
    question_end            TIMESTAMP,
    answer_type             TEXT        NOT NULL,
    question_text           TEXT,
    question_help_text      TEXT
);

CREATE TABLE IF NOT EXISTS answer_schema
(
    answer_schema_id        SERIAL      PRIMARY KEY,
    answer_schema_uuid      UUID        NOT NULL unique,
    answer_schema_code      TEXT        NOT NULL,
    question_schema_uuid    UUID        NOT NULL,
    answer_start            TIMESTAMP   NOT NULL,
    answer_end              TIMESTAMP,
    value                   TEXT,
    text                    TEXT,
    FOREIGN KEY (question_schema_uuid) REFERENCES question_schema (question_schema_uuid)
);

CREATE TABLE IF NOT EXISTS grouping
(
    group_id    SERIAL  PRIMARY KEY,
    group_uuid  UUID    NOT NULL unique,
    heading     TEXT    NOT NULL,
    subheading  TEXT,
    help_text   TEXT
);

CREATE TABLE IF NOT EXISTS question_group
(
    question_group_id       SERIAL      PRIMARY KEY,
    question_group_uuid     UUID        NOT NULL unique,
    question_schema_uuid    UUID        NOT NULL,
    group_uuid              UUID        NOT NULL,
    group_name              TEXT        NOT NULL,
    display_order           TEXT,
    mandatory               TEXT        NOT NULL,
    validation              TEXT,
    group_start             TIMESTAMP   NOT NULL,
    group_end               TIMESTAMP,
    FOREIGN KEY (question_schema_uuid) REFERENCES question_schema(question_schema_uuid),
    FOREIGN KEY (group_uuid) REFERENCES grouping(group_uuid)
);