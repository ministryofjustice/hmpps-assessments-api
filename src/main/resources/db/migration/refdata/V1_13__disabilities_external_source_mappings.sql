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

DELETE FROM question_schema WHERE question_code IN (
    'physical_disability',
    'physical_disability_details',
    'learning_disability',
    'learning_disability_details',
    'learning_difficulty',
    'learning_difficulty_details',
    'mental_health_condition',
    'mental_health_condition_details'
);

DELETE FROM question_group WHERE content_uuid IN (
    '7503ab1a-ea03-41f3-8d0a-b75da9830909',
    '4a97f15f-1a1e-44d5-83f7-ef5b2ad67246',
    'ce7451a2-5f75-42a8-95ad-377dee4d99bf',
    '637efa3b-264b-42f5-91d3-2985576e9614',
    '5c70e49d-92e1-4ee3-b934-380dca6e326a',
    '3c73385e-fafb-4df8-a668-8a8ab2e90625',
    'a349c303-b5e3-4c72-9540-fb17c1b6753a',
    'c97bb3af-993d-45f3-a349-27ba547310c6'
);

INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
('572425c8-be55-49b5-9a26-bcfee925d6dd', 'disabilities_adjustments', '2021-09-27 14:50:00', null, 'table', null, '', '', null);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
('03d7bf77-dc67-4ad7-b84c-63d94ed6f79f', 'disabilities_adjustments', 'UPW', 'DELIUS', '$.offenderProfile.disabilities', 'structured', 'secure/offenders/crn/$crn/all', null, false);
