CREATE TABLE IF NOT EXISTS external_source_question_mapping(
    external_source_question_mapping_id     SERIAL      PRIMARY KEY,
    external_source_question_mapping_uuid   UUID        NOT NULL UNIQUE,
    question_code                           VARCHAR(255) NOT NULL,
    assessment_schema_code                  VARCHAR(50) NOT NULL,
    external_source                         VARCHAR(50) NOT NULL,
    json_path_field                         VARCHAR(100) NOT NULL,
    field_type                              VARCHAR(50),
    FOREIGN KEY (assessment_schema_code) REFERENCES assessment_schema (assessment_schema_code),
    FOREIGN KEY (question_code) REFERENCES question_schema (question_code)
);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type)
VALUES
('4e0dd4c4-d2c0-4701-93a8-65ed879aa575', 'family_name', 'ROSH', 'COURT', 'name.surname', null),
('e872d57c-e501-4fd4-ae70-7e9dba9551ce', 'first_name', 'ROSH', 'COURT', 'name.forename1', null),
('a93464c2-1828-46ea-ad5d-5d4e6accbe23', 'dob', 'ROSH', 'COURT', 'defendantDob', 'date'),
('79cd9f1b-e06f-46ea-8125-8b29ec9c1e93', 'pnc', 'ROSH', 'COURT', 'pnc', null),
('b345f7bb-7398-4096-9802-c2599d762676', 'crn', 'ROSH', 'COURT', 'crn', null),
('a086610a-2135-40a4-ac88-da0534aee800', 'address_line_1', 'ROSH', 'COURT', 'defendantAddress.line1', null),
('f87a446d-5711-459a-b2b1-25118ed227f8', 'family_name', 'UPW', 'DELIUS', 'middleNames', 'array');
