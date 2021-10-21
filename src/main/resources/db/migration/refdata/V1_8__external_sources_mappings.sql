CREATE TABLE IF NOT EXISTS external_source_question_mapping(
    external_source_question_mapping_id     SERIAL      PRIMARY KEY,
    external_source_question_mapping_uuid   UUID        NOT NULL UNIQUE,
    question_code                           VARCHAR(255) NOT NULL,
    assessment_schema_code                  VARCHAR(50) NOT NULL,
    external_source                         VARCHAR(50) NOT NULL,
    json_path_field                         VARCHAR(1024) NOT NULL,
    field_type                              VARCHAR(50),
    FOREIGN KEY (assessment_schema_code) REFERENCES assessment_schema (assessment_schema_code),
    FOREIGN KEY (question_code) REFERENCES question_schema (question_code)
);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type)
VALUES
('4e0dd4c4-d2c0-4701-93a8-65ed879aa575', 'family_name', 'ROSH', 'COURT', 'name.surname', 'varchar'),
('e872d57c-e501-4fd4-ae70-7e9dba9551ce', 'first_name', 'ROSH', 'COURT', 'name.forename1', 'varchar'),
('a93464c2-1828-46ea-ad5d-5d4e6accbe23', 'dob', 'ROSH', 'COURT', 'defendantDob', 'date'),
('79cd9f1b-e06f-46ea-8125-8b29ec9c1e93', 'pnc', 'ROSH', 'COURT', 'pnc', 'varchar'),
('b345f7bb-7398-4096-9802-c2599d762676', 'crn', 'ROSH', 'COURT', 'crn', 'varchar'),
('a086610a-2135-40a4-ac88-da0534aee800', 'address_line_1', 'ROSH', 'COURT', 'defendantAddress.line1', 'varchar'),
--UPW
('ba5d21bb-ee85-496b-9148-7062a925d384', 'first_name', 'UPW', 'DELIUS', 'firstName', 'varchar'),
('e28fb88c-66ae-405e-9370-736e4225ff4a', 'first_name_aliases', 'UPW', 'DELIUS', '$.offenderAliases[*].[''firstName''])', 'array'),
('e8e7e586-660f-40e8-a5cf-c78870efab41', 'family_name', 'UPW', 'DELIUS', 'surname', 'varchar'),
('14796ba9-8f3a-48ee-b8b0-87f36b5c2e9e', 'family_name_aliases', 'UPW', 'DELIUS', '$.offenderAliases[*].[''surname''])', 'array'),
('c8e38291-dbc1-432e-a248-94f2a8bd653a', 'dob', 'UPW', 'DELIUS', 'dateOfBirth', 'varchar'),
('f9916f57-d398-43f1-a14d-9a5796d766d2', 'dob_aliases', 'UPW', 'DELIUS', '$.offenderAliases[*].[''dateOfBirth''])', 'array'),
('7b21ad52-a2b5-4ef2-aa11-bebb4f7ac52a', 'crn', 'UPW', 'DELIUS', 'otherIds.crn', 'varchar'),
('861bb639-61cf-4663-a2ef-55851135e8c5', 'pnc', 'UPW', 'DELIUS', 'otherIds.pncNumber', 'varchar'),
('2ee48b0b-3677-4a97-ab77-27d99053ef61', 'ethnicity', 'UPW', 'DELIUS', 'offenderProfile.ethnicity', 'varchar'),
('ebc6c624-eec2-48c8-86a2-6fe5bb52b84d', 'gender', 'UPW', 'DELIUS', 'gender', 'varchar'),
('7a129e3a-fe40-4138-9832-643404982a2c', 'email_addresses', 'UPW', 'DELIUS', '$.contactDetails.emailAddresses[*]', 'array'),
('4eb108ac-bbb7-416b-8d05-64283bdc6df5', 'mobile_phone_number', 'UPW', 'DELIUS', '$.contactDetails.phoneNumbers[?(@.type==''MOBILE'')].number', 'array'),

('0a9efa70-9f42-41ff-96da-a814bed4befe', 'upw_physical_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D02'' || @.disabilityType.code==''RM'' || @.disabilityType.code==''RC'' || @.disabilityType.code==''PC'' || @.disabilityType.code==''VI'' || @.disabilityType.code==''HD'')].disabilityType.code', 'array'),
('a9258dbd-9b16-4511-95c3-52aa7a95f948', 'upw_physical_disability_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D02'' || @.disabilityType.code==''RM'' || @.disabilityType.code==''RC'' || @.disabilityType.code==''PC'' || @.disabilityType.code==''VI'' || @.disabilityType.code==''HD'')].disabilityType.description', 'array'),
('d6920b7b-927e-407a-b96c-a738d92b0bee', 'upw_learning_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LA'')].disabilityType.code', 'array'),
('27a0cc22-a876-4123-9a34-56363d4ce24f', 'upw_learning_disability_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LA'')].disabilityType.description', 'array'),
('7cad64f6-898d-4e2f-a466-6d660b76380b', 'upw_learning_difficulty', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LD'')].disabilityType.code', 'array'),
('0a8f5e55-1223-4912-94a7-7fad8dc28368', 'upw_learning_difficulty_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LD'')].disabilityType.description', 'array'),
('12be7d28-b20e-43f3-ba3b-3d0fd302e63a', 'upw_mental_health_condition', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D01'' || @.disabilityType.code==''MI'')].disabilityType.code', 'array'),
('031e620a-b49a-4371-931b-6d6e05c3af4d', 'upw_mental_health_condition_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D01'' || @.disabilityType.code==''MI'')].disabilityType.description', 'array')
;