CREATE TABLE IF NOT EXISTS external_source_question_mapping(
    external_source_question_mapping_id    SERIAL      PRIMARY KEY,
    external_source_question_mapping_uuid  UUID        NOT NULL UNIQUE,
    question_schema_uuid    UUID        NOT NULL,
    assessment_schema_code  VARCHAR(50)     NOT NULL,
    external_source VARCHAR(50)     NOT NULL,
    json_path_field VARCHAR(100)     NOT NULL,
    field_type      VARCHAR(50),
    FOREIGN KEY (assessment_schema_code) REFERENCES assessment_schema (assessment_schema_code),
    FOREIGN KEY (question_schema_uuid) REFERENCES question_schema (question_schema_uuid)
);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_schema_uuid, assessment_schema_code, external_source, json_path_field, field_type)
VALUES
('4e0dd4c4-d2c0-4701-93a8-65ed879aa575', '33923c1e-e3ba-4c02-ba42-3b8d828f9e18', 'ROSH', 'COURT', 'name.surname', null),
('e872d57c-e501-4fd4-ae70-7e9dba9551ce', '2d6f7515-dd03-4cfc-b365-6739e1251498', 'ROSH', 'COURT', 'name.forename1', null),
('a93464c2-1828-46ea-ad5d-5d4e6accbe23', '83612e87-a57c-4b20-ab6d-76148e61a37b', 'ROSH', 'COURT', 'defendantDob', 'date'),
('79cd9f1b-e06f-46ea-8125-8b29ec9c1e93', '49ec3bae-2726-4a43-84de-0e63e9f98bb1', 'ROSH', 'COURT', 'pnc', null),
('b345f7bb-7398-4096-9802-c2599d762676', '558c142a-b3d1-4a5c-a1e4-12917d8353e0', 'ROSH', 'COURT', 'crn', null),
('a086610a-2135-40a4-ac88-da0534aee800', 'f3e03d17-015c-46d7-bf33-52c5d8503c08', 'ROSH', 'COURT', 'defendantAddress.line1', null),
('f87a446d-5711-459a-b2b1-25118ed227f8', '33923c1e-e3ba-4c02-ba42-3b8d828f9e18', 'UPW', 'DELIUS', 'name.middleNames', null);
