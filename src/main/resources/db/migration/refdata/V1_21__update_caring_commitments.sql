-- add individual disability questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('8f721d80-2b61-4b3d-8f72-7d20a72d2ed0', 'active_carer_commitments', '2021-09-27 14:50:00', null, 'freetext', null, 'Are there carer commitments?', '', null),
    ('d1d6d2be-bbec-4cd0-bced-927bf24fa614', 'active_carer_commitments_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Additional information (Optional)', '', null);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_type, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
    ('c6dabd44-065c-4419-a696-f993c50860b3', 'active_carer_commitments', 'UPW', 'DELIUS', '$.personalCircumstances[?((@.personalCircumstanceType.code==''I'') && (@.isActive==true))]', 'structured', 'secure/offenders/crn/$crn/personalCircumstances', null, false);

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('673a829f-7dd5-4064-9703-6e5ddeaec852', '8f721d80-2b61-4b3d-8f72-7d20a72d2ed0', 'question', '28d07199-ceed-473f-8584-156b018d967a', 4, true),
    ('103d0a46-ffcd-4c28-b807-7b2629e7b19f', 'd1d6d2be-bbec-4cd0-bced-927bf24fa614', 'question', '28d07199-ceed-473f-8584-156b018d967a', 5, true);
