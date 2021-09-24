-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS subject
(
    subject_id          SERIAL          PRIMARY KEY,
    subject_uuid        UUID            UNIQUE,
    name                TEXT            NOT NULL,
    oasys_offender_pk   NUMERIC         NULL,
    pnc                 TEXT            NULL,
    crn                 VARCHAR(255)    NOT NULL UNIQUE,
    date_of_birth       DATE            NOT NULL,
    created_date        TIMESTAMP       NOT NULL
);