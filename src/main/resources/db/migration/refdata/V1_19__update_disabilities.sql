-- Remove disability grouping questions
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
DELETE FROM question WHERE question_code IN (
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

-- add individual disability questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('91f02777-6c45-4d7f-b3cb-42737089ed3b', 'autistic_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Autism Spectrum Condition (ASC)', null, null),
    ('62088094-85dc-447d-899d-c1d3ca448b0d', 'dyslexia_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Dyslexia', null, null),
    ('c8f62544-09dc-4ab1-b95f-2d32851bb75e', 'visual_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Visual condition', null, null),
    ('bf89af74-b61b-4b65-b336-44102da2f418', 'speech_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Speech condition', null, null),
    ('decc4668-a3a0-4a64-98b0-f811d308764a', 'hearing_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Hearing condition', null, null),
    ('e25eb4ab-b057-44a0-baed-2efeb71f57e2', 'learning_difficulties', '2021-09-27 14:50:00', null, 'textarea', null, 'Learning difficulties', null, null),
    ('ba3df794-4512-45f2-9aa9-ad8c7f18338d', 'learning_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Learning disability or challenges', null, null),
    ('980386d9-cfee-4741-ae68-5f7bd318487e', 'mental_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Mental health condition', null, null),
    ('cf3011b5-49ef-45b2-bd9e-c790ba88e512', 'progressive_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Progressive condition', null, null),
    ('47bf7146-3879-4b38-b8b5-cd7cba95e8d5', 'mobility_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Reduced mobility', null, null),
    ('72361e0f-b6e4-4ecb-95ae-0e5af015f0ef', 'physical_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Reduced physical ability', null, null),
    ('0e37d532-a3e2-4606-a149-b7bda9e4bfc7', 'disfigurement_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Disfigurement', null, null),
    ('5a8d2602-e48d-4c46-b8a1-95aed3ef57be', 'refuse_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'Refusal to disclose', null, null),
    ('3efe1db6-e349-4903-9cff-cc76af537941', 'no_disability', '2021-09-27 14:50:00', null, 'textarea', null, 'No disability', null, null),

    ('0af7f330-0716-4018-b8ef-07a92e2b655d', 'additional_disabilities', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Any additional disabilities or health issues that affect the individual’s ability to engage with Community Payback?', '', null),
    ('d0e077b1-5cf5-41c3-8d94-c84323e643a8', 'additional_disabilities_adjustments', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', '', null);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_type, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
    ('080259f7-ef91-49af-8262-d7fc0240235b', 'autistic_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''AS''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('8e1dca63-0094-4515-a17e-5e552bedd637', 'dyslexia_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''DY''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('20ed6bb6-33cf-4b2d-83a0-99fa5211ebd8', 'visual_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''VI''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('51a0e59f-2d52-4c91-b90b-21640b5ba915', 'speech_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''SI''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('358b6c13-3ff5-4184-8865-fc6721936d25', 'hearing_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''HD''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('312d9d0e-2734-48ca-a4c4-e7e17a8cbb8b', 'learning_difficulties', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LD''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('6ec9aae0-0950-4701-99b0-1589584197b0', 'learning_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LA''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('0f01a375-903d-4707-ade7-1d8c331afa9a', 'mental_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''MI''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('10fca145-2451-4d18-8006-5e402db32032', 'progressive_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''PC''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('4b3b2a12-4511-4a65-95b0-5506927da524', 'mobility_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''RM''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('7461b171-b4d8-438d-a491-2aee4ac8a27a', 'physical_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''RC''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('889dc81f-7985-49c3-b395-4ade2bcd0181', 'disfigurement_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''SD''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('5a6256f5-ed5b-4609-a208-6df04ff73e98', 'refuse_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''RF''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false),
    ('7d1cb832-beee-4fd4-9f7e-309d630368eb', 'no_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''OD''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/all', null, false);
