CREATE TABLE IF NOT EXISTS clone_assessment_excluded_questions (
    clone_assessment_excluded_questions_id SERIAL PRIMARY KEY,
    assessment_schema_code VARCHAR(50) NOT NULL,
    question_code          VARCHAR(255) NOT NULL,
    FOREIGN KEY (assessment_schema_code) REFERENCES assessment_schema (assessment_schema_code),
    FOREIGN KEY (question_code) REFERENCES question_schema (question_code),
    CONSTRAINT clone_assessment_excluded_questions_unique UNIQUE (assessment_schema_code, question_code)
);

INSERT INTO clone_assessment_excluded_questions(assessment_schema_code, question_code)
VALUES ('UPW', 'individual_details_complete'),
       ('UPW', 'cultural_religious_adjustment_complete'),
       ('UPW', 'placement_preference_complete'),
       ('UPW', 'placement_preference_by_gender_complete'),
       ('UPW', 'maturity_assessment_details_complete'),
       ('UPW', 'rosh_community_complete'),
       ('UPW', 'managing_risk_complete'),
       ('UPW', 'disabilities_complete'),
       ('UPW', 'health_issues_complete'),
       ('UPW', 'gp_details_complete'),
       ('UPW', 'travel_information_complete'),
       ('UPW', 'caring_commitments_complete'),
       ('UPW', 'employment_education_skills_complete'),
       ('UPW', 'employment_training_complete'),
       ('UPW', 'eligibility_intensive_working_complete'),
       ('UPW', 'individual_availability_complete'),
       ('UPW', 'equipment_complete');

