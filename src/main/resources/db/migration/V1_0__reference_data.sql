CREATE TABLE IF NOT EXISTS assessment_schema
(
    assessment_schema_id       SERIAL PRIMARY KEY,
    assessment_schema_uuid     UUID        NOT NULL unique,
    assessment_schema_code     TEXT        NOT NULL,
    oasys_assessment_type      VARCHAR(50) NULL,
    oasys_create_assessment_at TEXT,
    assessment_name            TEXT
);

CREATE TABLE IF NOT EXISTS answer_schema_group
(
    answer_schema_group_id   SERIAL PRIMARY KEY,
    answer_schema_group_uuid UUID      NOT NULL unique,
    answer_schema_group_code TEXT      NOT NULL,
    group_start              TIMESTAMP NOT NULL,
    group_end                TIMESTAMP
);

CREATE TABLE IF NOT EXISTS answer_schema
(
    answer_schema_id         SERIAL PRIMARY KEY,
    answer_schema_uuid       UUID      NOT NULL unique,
    answer_schema_code       TEXT      NOT NULL,
    answer_schema_group_uuid UUID      NOT NULL,
    answer_start             TIMESTAMP NOT NULL,
    answer_end               TIMESTAMP,
    value                    TEXT,
    text                     TEXT,
    FOREIGN KEY (answer_schema_group_uuid) REFERENCES answer_schema_group (answer_schema_group_uuid)
);

CREATE TABLE IF NOT EXISTS question_schema
(
    question_schema_id       SERIAL PRIMARY KEY,
    question_schema_uuid     UUID      NOT NULL unique,
    question_code            TEXT      NOT NULL,
    oasys_question_code      TEXT,
    external_source          TEXT,
    question_start           TIMESTAMP NOT NULL,
    question_end             TIMESTAMP,
    answer_type              TEXT      NOT NULL,
    answer_schema_group_uuid UUID      NULL,
    question_text            TEXT,
    question_help_text       TEXT,
    reference_data_category  TEXT,
    FOREIGN KEY (answer_schema_group_uuid) REFERENCES answer_schema_group (answer_schema_group_uuid)
);

CREATE TABLE IF NOT EXISTS oasys_question_mapping
(
    mapping_id           SERIAL PRIMARY KEY,
    mapping_uuid         UUID NOT NULL unique,
    question_schema_uuid UUID NOT NULL unique,
    ref_section_code     TEXT NOT NULL,
    logical_page         INTEGER,
    ref_question_code    TEXT NOT NULL,
    fixed_field          BOOLEAN,
    FOREIGN KEY (question_schema_uuid) REFERENCES question_schema (question_schema_uuid)
);

CREATE TABLE IF NOT EXISTS oasys_reference_data_target_mapping
(
    id                          SERIAL PRIMARY KEY,
    question_schema_uuid        UUID    NOT NULL,
    parent_question_schema_uuid UUID    NOT NULL,
    is_required                 BOOLEAN NOT NULL,
    FOREIGN KEY (question_schema_uuid) REFERENCES question_schema (question_schema_uuid),
    FOREIGN KEY (parent_question_schema_uuid) REFERENCES question_schema (question_schema_uuid)
);

CREATE TABLE IF NOT EXISTS grouping
(
    group_id    SERIAL PRIMARY KEY,
    group_uuid  UUID      NOT NULL unique,
    group_code  TEXT      NOT NULL,
    heading     TEXT      NOT NULL,
    subheading  TEXT,
    help_text   TEXT,
    group_start TIMESTAMP NOT NULL,
    group_end   TIMESTAMP
);

CREATE TABLE IF NOT EXISTS assessment_schema_groups
(
    assessment_schema_group_id SERIAL PRIMARY KEY,
    assessment_schema_uuid     UUID NOT NULL,
    group_uuid                 UUID NOT NULL,
    FOREIGN KEY (assessment_schema_uuid) REFERENCES assessment_schema (assessment_schema_uuid),
    FOREIGN KEY (group_uuid) REFERENCES grouping (group_uuid)
);

CREATE TABLE IF NOT EXISTS question_group
(
    question_group_id   SERIAL PRIMARY KEY,
    question_group_uuid UUID    NOT NULL unique,
    group_uuid          UUID    NOT NULL,
    content_uuid        UUID    NOT NULL,
    content_type        TEXT    NOT NULL,
    display_order       INTEGER NOT NULL,
    mandatory           BOOLEAN NOT NULL,
    validation          TEXT,
    read_only           BOOLEAN NOT NULL,
    CONSTRAINT check_content_type CHECK (content_type = 'question' OR content_type = 'group'),
    FOREIGN KEY (group_uuid) REFERENCES grouping (group_uuid)
);