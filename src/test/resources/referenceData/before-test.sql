-- noinspection SqlResolveForFile
INSERT INTO hmppsassessmentsschemas.answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES ('f756f79d-dfad-49f9-a1b9-964a41cf660d', 'TEST', '2019-11-14 08:11:53.177108', null);

INSERT INTO hmppsassessmentsschemas.answer_schema (answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES ('464e25da-f843-43b6-8223-4af415abda0c', 'RSR_01a','f756f79d-dfad-49f9-a1b9-964a41cf660d', '2019-11-14 08:11:53.177108', null, 'true', 'Yes'),
       ('0a428566-6393-462f-addb-50feaaf75d57', 'RSR_01b','f756f79d-dfad-49f9-a1b9-964a41cf660d', '2019-11-14 08:11:53.177108', null, 'false', 'No');

INSERT INTO hmppsassessmentsschemas.question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text)
VALUES ('fd412ca8-d361-47ab-a189-7acb8ae0675b', 'RSR_01', '2019-11-14 08:11:53.177108', null, 'radio', 'f756f79d-dfad-49f9-a1b9-964a41cf660d', 'Question text', 'Help text'),
       ('1948af63-07f2-4a8c-9e4c-0ec347bd6ba8', 'RSR_01_conditional', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('a5830801-533c-4b9e-bab1-03272718d6dc', 'OASys_mapped', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('a8e303f5-5f88-4343-94d1-a369ca1f86cb', 'OASys_mapped_to_fixed', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('b9dd3680-c4d6-403e-8f27-8d65481cbf44', 'RSR_02', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111112', 'RSR_05', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111113', 'RSR_06', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111115', 'RSR_07', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111116', 'RSR_08', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111117', 'RSR_09', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111118', 'RSR_10', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text'),
       ('11111111-1111-1111-1111-111111111119', 'RSR_11', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text')
;

INSERT INTO hmppsassessmentsschemas.oasys_question_mapping(mapping_uuid, question_schema_uuid, ref_section_code, logical_page, ref_question_code)
VALUES ('204b461b-90af-4e11-b57f-7ccb07b67059', 'a5830801-533c-4b9e-bab1-03272718d6dc', 'RSR', '1', 'RSR_02');

INSERT INTO hmppsassessmentsschemas.oasys_question_mapping(mapping_uuid, question_schema_uuid, ref_section_code, ref_question_code, fixed_field)
VALUES ('5bfbc30d-811f-443e-8f82-8d86eaadbbe4', 'a8e303f5-5f88-4343-94d1-a369ca1f86cb', 'OFFIN', 'test_field', true);


INSERT INTO hmppsassessmentsschemas.grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('e964d699-cf96-4abd-af0e-ddf1f6687a46', 'assessment', 'Assessement', 'Subheading 1', 'Help text', '2019-11-14 08:11:53.177108', null),
       ('e353f3df-113d-401c-a3c0-14239fc17cf9', 'Group code', 'Heading 1', 'Subheading 1', 'Help text', '2019-11-14 08:11:53.177108', null),
       ('6afbe596-9956-4620-824b-c6c9000ace7c', 'Subgroup code', 'Second level heading', '', '', '2019-11-14 08:11:53.177108', null);


INSERT INTO hmppsassessmentsschemas.question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('334f3e21-b249-4c7f-848e-05c0d2aad8f4', 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'question', 'e353f3df-113d-401c-a3c0-14239fc17cf9', '1', true, null, false),
       ('fcec5c32-ea96-424c-80a5-8186dc414619', '1948af63-07f2-4a8c-9e4c-0ec347bd6ba8', 'question', 'e353f3df-113d-401c-a3c0-14239fc17cf9', '2', false, null, false),
       ('c1d9281d-2363-43a7-9e02-bd19c13d685f', 'e353f3df-113d-401c-a3c0-14239fc17cf9', 'group', 'e964d699-cf96-4abd-af0e-ddf1f6687a46', '1', false, null, false),
       ('67b942c8-86f6-4493-af53-9f814b41f344', '6afbe596-9956-4620-824b-c6c9000ace7c', 'group', 'e353f3df-113d-401c-a3c0-14239fc17cf9', '3', false, null, false),
       ('6c0c874f-cd71-4422-b153-2cb270183b5c', 'b9dd3680-c4d6-403e-8f27-8d65481cbf44', 'question', '6afbe596-9956-4620-824b-c6c9000ace7c', '3', false, null, false);

/* Question Dependency */
insert into hmppsassessmentsschemas.question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES ('11111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111112', 'Y', '2020-1-14 09:00', true),
       ('11111111-1111-1111-1111-111111111116', '11111111-1111-1111-1111-111111111115', 'Y', '2020-1-14 09:00', true),
       ('11111111-1111-1111-1111-111111111117', '11111111-1111-1111-1111-111111111116', 'Y', '2020-1-14 09:00', true),
       ('11111111-1111-1111-1111-111111111118', '11111111-1111-1111-1111-111111111116', 'N', '2020-1-14 09:00', true),
       ('11111111-1111-1111-1111-111111111119', '11111111-1111-1111-1111-111111111116', 'Y', '2020-1-14 09:00', false),
       ('1948af63-07f2-4a8c-9e4c-0ec347bd6ba8', 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'true', '2020-1-14 09:00', true);

INSERT INTO hmppsassessmentsschemas.assessment_schema_groups(assessment_schema_uuid, group_uuid)
VALUES ('51c2e87e-a540-4027-8f5a-e6c80511332f', 'e353f3df-113d-401c-a3c0-14239fc17cf9'),
       ('c3a6beac-37c0-46b6-b4b3-62086b624675', 'e964d699-cf96-4abd-af0e-ddf1f6687a46')
ON CONFLICT DO NOTHING;