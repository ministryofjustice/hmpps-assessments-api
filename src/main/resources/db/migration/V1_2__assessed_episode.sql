CREATE TABLE IF NOT EXISTS assessed_episode
(
    episode_id      SERIAL      PRIMARY KEY,
    user_id         TEXT        NOT NULL,
    created_date    TIMESTAMP   NOT NULL,
    change_reason   TEXT        NOT NULL,
    assessment_id   SERIAL      NOT NULL,
    FOREIGN KEY (assessment_id)
        REFERENCES assessment(assessment_id)
);

ALTER TABLE question_answer
    ADD CONSTRAINT fk_ep_qa
    FOREIGN KEY (episode_id) REFERENCES assessed_episode(episode_id);
