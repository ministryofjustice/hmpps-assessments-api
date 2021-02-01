-- noinspection SqlResolveForFile
DELETE FROM answer_schema WHERE true;
DELETE FROM question_schema WHERE true;
DELETE FROM answer_schema_group WHERE true;

DELETE FROM question_group WHERE true;
DELETE FROM grouping WHERE true;
DELETE FROM question_dependency WHERE true;

INSERT INTO answer_schema_group (answer_schema_group_id, answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES (0, 'f756f79d-dfad-49f9-a1b9-964a41cf660d', 'TEST', '2019-11-14 08:11:53.177108', null);

INSERT INTO answer_schema (answer_schema_id, answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES (0, '464e25da-f843-43b6-8223-4af415abda0c', 'RSR_01a','f756f79d-dfad-49f9-a1b9-964a41cf660d', '2019-11-14 08:11:53.177108', null, 'true', 'Yes'),
       (1, '0a428566-6393-462f-addb-50feaaf75d57', 'RSR_01b','f756f79d-dfad-49f9-a1b9-964a41cf660d', '2019-11-14 08:11:53.177108', null, 'false', 'No');

INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text)
VALUES (0, 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'RSR_01', 'RSR_01', '2019-11-14 08:11:53.177108', null, 'radio', 'f756f79d-dfad-49f9-a1b9-964a41cf660d', 'Question text', 'Help text'),
       (1, '1948af63-07f2-4a8c-9e4c-0ec347bd6ba8', 'RSR_01_conditional', 'RSR_01_conditional', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Question text', 'Help text');

INSERT INTO grouping (group_id, group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES (0, 'e353f3df-113d-401c-a3c0-14239fc17cf9', 'Group code', 'Heading 1', 'Subheading 1', 'Help text', '2019-11-14 08:11:53.177108', null);

INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES (0, '334f3e21-b249-4c7f-848e-05c0d2aad8f4', 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'question', 'e353f3df-113d-401c-a3c0-14239fc17cf9', '1', true, null ),
       (1, 'fcec5c32-ea96-424c-80a5-8186dc414619', '1948af63-07f2-4a8c-9e4c-0ec347bd6ba8', 'question', 'e353f3df-113d-401c-a3c0-14239fc17cf9', '2', false, null );

/* Question Dependency */
insert into question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline) values
('11111111-1111-1111-1111-111111111113', '11111111-1111-1111-1111-111111111112', 'y', '2020-1-14 09:00', true),
('11111111-1111-1111-1111-111111111116', '11111111-1111-1111-1111-111111111115', 'y', '2020-1-14 09:00', true),
('1948af63-07f2-4a8c-9e4c-0ec347bd6ba8', 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'true', '2020-1-14 09:00', true);
