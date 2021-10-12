-- noinspection SqlResolve,SqlResolve

CREATE TABLE IF NOT EXISTS assessed_episode
(
    episode_id            SERIAL       PRIMARY KEY,
    episode_uuid          UUID         UNIQUE,
    assessment_schema_code VARCHAR(50) NOT NULL,
    oasys_set_pk          BIGINT       NULL,
    created_date          TIMESTAMP    NOT NULL,
    end_date              TIMESTAMP,
    change_reason         TEXT         NOT NULL,
    assessment_uuid       UUID         NOT NULL,
    offence_uuid          UUID,
    author_uuid           UUID,
    answers               JSONB,
    tables                JSONB,
    FOREIGN KEY (assessment_uuid) REFERENCES assessment (assessment_uuid)
);
