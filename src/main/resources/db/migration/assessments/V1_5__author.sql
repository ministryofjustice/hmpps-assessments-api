-- noinspection SqlResolveForFile

CREATE TABLE IF NOT EXISTS author
(
    author_id       SERIAL          PRIMARY KEY,
    author_uuid     UUID            UNIQUE,
    user_id         TEXT NOT NULL,
    user_source     TEXT NOT NULL,
    user_name       TEXT NOT NULL,
    user_full_name  TEXT NOT NULL
);

ALTER TABLE assessed_episode ADD FOREIGN KEY (author_uuid) REFERENCES author(author_uuid)
;