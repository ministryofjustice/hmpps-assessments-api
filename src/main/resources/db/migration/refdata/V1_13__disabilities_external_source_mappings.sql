DELETE FROM external_source_question_mapping WHERE question_code IN (
    'physical_disability',
    'physical_disability_details',
    'learning_disability',
    'learning_disability_details',
    'learning_difficulty',
    'learning_difficulty_details',
    'mental_health_condition',
    'mental_health_condition_details'
);
--
-- DELETE FROM question_schema WHERE question_code IN (
--     'physical_disability',
--     'physical_disability_details',
--     'learning_disability',
--     'learning_disability_details',
--     'learning_difficulty',
--     'learning_difficulty_details',
--     'mental_health_condition',
--     'mental_health_condition_details'
-- );

-- INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, reference_data_category)
-- VALUES
-- ('7503ab1a-ea03-41f3-8d0a-b75da9830909', 'disabilities_and_mental_health', '2021-09-27 14:50:00', null, 'table', null, '', '', null);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
('03d7bf77-dc67-4ad7-b84c-63d94ed6f79f', 'disabilities_array', 'UPW', 'DELIUS', '$.offenderProfile.disabilities', 'structured', 'secure/offenders/crn/$crn/all', null, false);
