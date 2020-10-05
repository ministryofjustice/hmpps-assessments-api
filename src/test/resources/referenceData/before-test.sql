-- noinspection SqlResolveForFile

INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, question_text, question_help_text)
VALUES (0, 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'RSR_01', 'RSR_01', '2019-11-14 08:11:53.177108', null, 'radio', 'Question text', 'Help text');

INSERT INTO answer_schema (answer_schema_id, answer_schema_uuid, answer_schema_code, question_schema_uuid, answer_start, answer_end, value, text)
VALUES (0, '464e25da-f843-43b6-8223-4af415abda0c', 'RSR_01a','fd412ca8-d361-47ab-a189-7acb8ae0675b', '2019-11-14 08:11:53.177108', null, 'value', 'text');

INSERT INTO grouping (group_id, group_uuid, heading, subheading, help_text)
VALUES (0, 'e353f3df-113d-401c-a3c0-14239fc17cf9', 'Heading 1', 'Subheading 1', 'Help text');

INSERT INTO question_group (question_group_id, question_group_uuid, question_schema_uuid, group_uuid, group_name, parent_group_uuid, display_order, mandatory, validation, group_start, group_end)
VALUES (0, '334f3e21-b249-4c7f-848e-05c0d2aad8f4', 'fd412ca8-d361-47ab-a189-7acb8ae0675b', 'e353f3df-113d-401c-a3c0-14239fc17cf9', 'Group name', null, '1', 'mandatory', null, '2019-11-14 08:11:53.177108', null );