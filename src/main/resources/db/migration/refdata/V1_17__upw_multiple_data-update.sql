DELETE FROM external_source_question_mapping
WHERE external_source_question_mapping_uuid in
    ('8e219d36-6979-4018-8944-12777990910e',
    '46d7ffe0-705e-4027-95ed-359381b2f0d0',
    '9bad074d-bfe0-4bc3-ba2e-0a5573b5d174',
    'c0483847-1e87-4061-a7bb-a44f615a0881',
    'ef0a997e-66d5-40f4-958c-b4ed84c9807e',
    '0645aed0-fe8d-49ea-89d7-c5fb167ceef9',
    'd19f2e04-d452-41a4-87b0-de7cef7e9fe9',
    '61801492-1b02-40ad-9a05-b8602e7bbc5a',
    '86d4a8e6-f51b-49c2-9d01-5f452729f752',
    'c635434d-4c24-4780-995d-a7d44a55dada',
    '6f8bd761-dc11-4c72-b2ab-5e1ee8a0038f',
    'f7318c93-0929-4a10-bd6a-1f7e4674dc11',
    '57099ea6-37e0-441e-9d05-ca840d225261',
    'a9ec6f88-58ad-4339-8969-3c27a62c85db',
    '8b7c2dac-620a-4fd9-9f78-016ed53511a4',
    '873c2901-6f23-4a7f-b230-fafba2819faf',
    'b1c76706-8634-43c9-8c70-67b78976d672',
    '4283f9e3-0ec5-4f40-831c-0c93160d5c43',
    '7501f047-2836-4bb8-8e03-54ab40543a8b',
    '926c91e1-d020-4b16-9040-acd9b0dc1d53',
    '7762598e-dc3c-41a4-8546-38444c0ef708',
    '7802580e-fa85-4998-966e-cc29e58f04a5');

INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('b8f51e03-c2d6-4ec5-b539-30ba94524d47', 'emergency_contact_details', '2021-09-27 14:50:00', null, 'table', null, '', null, null),
    ('2a1a41dd-5d77-4674-9f8f-561c654867b8', 'gp_details', '2021-09-27 14:50:00', null, 'table', null, '', '', null);


INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
    ('8e219d36-6979-4018-8944-12777990910e', 'emergency_contact_details', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/personalContacts', null, false),
    ('cb24dc64-e8fd-4960-8096-1907d59de3b0', 'gp_details', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02''&&@.isActive==true)]', 'structured', 'secure/offenders/crn/$crn/personalContacts', null, false);