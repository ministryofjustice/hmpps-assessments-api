-- add individual disability questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('7268c822-637c-45ad-b23a-6ee211e4c206', 'active_disabilities', '2021-09-27 14:50:00', null, 'freetext', null, 'Disabilities', '', null),
    ('22aa55c0-8cd3-4106-a4cf-a64c05ed72c7', 'additional_disabilities', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Any additional disabilities or health issues that affect the individual’s ability to engage with Community Payback?', '', null),
    ('7ce51aeb-c2fa-4b95-9c0d-8e353b5de869', 'additional_disabilities_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_type, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
    ('9b78dd50-2300-478a-a3d8-babc8bbe165e', 'active_disabilities', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?((@.disabilityType.code==''AP'' || @.disabilityType.code==''DY'' || @.disabilityType.code==''VI'' || @.disabilityType.code==''SI'' || @.disabilityType.code==''HD'' || @.disabilityType.code==''LD'' || @.disabilityType.code==''RD'' || @.disabilityType.code==''MI'' || @.disabilityType.code==''PC'' || @.disabilityType.code==''RM'' || @.disabilityType.code==''RC'' || @.disabilityType.code==''SD'' || @.disabilityType.code==''RF'' || @.disabilityType.code==''OD'' || @.disabilityType.code==''AS'' || @.disabilityType.code==''LA'') && (@.isActive==true))]', 'structured', 'secure/offenders/crn/$crn/all', null, false);

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('96c3ef28-3f35-4516-8483-b44f7119f771', '7268c822-637c-45ad-b23a-6ee211e4c206', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 12, true),
    ('d75fd307-0688-42f8-b1e1-e1e85a146e39', '22aa55c0-8cd3-4106-a4cf-a64c05ed72c7', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 13, false),
    ('01a7f461-d1da-4385-a7c6-90c7a6812ff4', '7ce51aeb-c2fa-4b95-9c0d-8e353b5de869', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 14, false);

INSERT INTO question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES
    ('7ce51aeb-c2fa-4b95-9c0d-8e353b5de869', '22aa55c0-8cd3-4106-a4cf-a64c05ed72c7', 'YES', '2020-11-30 14:50:00', true);
