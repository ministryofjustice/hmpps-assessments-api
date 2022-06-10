-- add individual disability questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('7268c822-637c-45ad-b23a-6ee211e4c206', 'active_disabilities', '2021-09-27 14:50:00', null, 'freetext', null, 'Disabilities', '', null);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_type, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
    ('9b78dd50-2300-478a-a3d8-babc8bbe165e', 'active_disabilities', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?((@.disabilityType.code==''AS'' || @.disabilityType.code==''DY'' || @.disabilityType.code==''VI'' || @.disabilityType.code==''SI'' || @.disabilityType.code==''HD'' || @.disabilityType.code==''LD'' || @.disabilityType.code==''LA'' || @.disabilityType.code==''MI'' || @.disabilityType.code==''PC'' || @.disabilityType.code==''RM'' || @.disabilityType.code==''RC'' || @.disabilityType.code==''SD'' || @.disabilityType.code==''RF'' || @.disabilityType.code==''OD'') && (@.isActive==true))]', 'structured', 'secure/offenders/crn/$crn/all', null, false);

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('96c3ef28-3f35-4516-8483-b44f7119f771', '7268c822-637c-45ad-b23a-6ee211e4c206', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 12, false);