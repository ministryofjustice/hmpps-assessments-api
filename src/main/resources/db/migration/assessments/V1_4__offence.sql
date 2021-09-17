-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS offence
(
    offence_id            SERIAL          PRIMARY KEY,
    offence_uuid          UUID            UNIQUE,
    source                TEXT            NOT NULL,
    source_id             TEXT            NOT NULL,
    offence_code          TEXT,
    code_description      TEXT,
    offence_subcode       TEXT,
    subcode_description   TEXT,
    sentence_date         DATE
);

ALTER TABLE assessed_episode
    ADD FOREIGN KEY (offence_uuid)
        REFERENCES offence(offence_uuid)
;