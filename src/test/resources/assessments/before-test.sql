-- noinspection SqlResolveForFile

delete from assessed_episode where true;
delete from subject where true;
delete from assessment where true;
DELETE FROM oasys_question_mapping WHERE true;
DELETE FROM answer_schema WHERE true;
DELETE FROM oasys_reference_data_target_mapping WHERE true;
DELETE FROM question_schema WHERE true;
DELETE FROM answer_schema_group WHERE true;
DELETE FROM question_group WHERE true;
DELETE FROM grouping WHERE true;
DELETE FROM question_dependency WHERE true;


/* Assessment with Episodes */
insert into assessment  (assessment_id, assessment_uuid, created_date) values
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', '2019-11-14 09:00'),
(2, '19c8d211-68dc-4692-a6e2-d58468127056', '2019-11-14 09:00');

insert into subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(1, 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');

insert into assessed_episode  (episode_id, episode_uuid, user_id, created_date, end_date, change_reason, assessment_uuid, answers) values
(1, 'd7aafe55-0cff-4f20-a57a-b66d79eb9c91', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}'),
(2, 'f3569440-efd5-4289-8fdd-4560360e5259', 'USER1', '2019-11-14 09:00', null,'More Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}');

/* Empty assessment */
insert into assessment  (assessment_id, assessment_uuid, created_date) values
(3, 'f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8', '2020-1-14 09:00');

/* Episodes to complete */
insert into assessment  (assessment_id, assessment_uuid, created_date) values
(4, 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '2020-1-14 09:00'),
(5, '6082265e-885d-4526-b713-77e59b70691e', '2020-1-14 09:00'),
(6, 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '2020-1-14 09:00');

insert into subject (subject_id, subject_uuid, source, source_id, name, oasys_offender_pk, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(3, '7bce2323-fefa-42eb-b622-ec65747aae56', 'COURT', 'courtCode|caseNumber2', 'John Smith', 1, 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84'),
(4, '1146f644-dfb9-4e6d-9446-1be089538480', 'COURT', 'courtCode|caseNumber3', 'John Smith', 12345, 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '6082265e-885d-4526-b713-77e59b70691e'),
(5, 'f6023241-ba22-47e4-bc7d-f7adfde4276c', 'COURT', 'courtCode|caseNumber3', 'John Smith', 5, 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5');

insert into assessed_episode  (episode_id, episode_uuid, user_id, assessment_schema_code, oasys_set_pk, created_date, end_date, change_reason, assessment_uuid, answers) values
(3, '163cf020-ff53-4dc6-a15c-e93e8537d347', 'USER1', 'ROSH', 1, '2019-11-14 09:00', null, 'More Change of Circs', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '{}'),
(4, '461994f9-86b9-4177-8412-de8dbb18415b', 'USER1', 'ROSH', 5678, '2019-11-14 09:00', '2019-11-14 12:00', 'More Change of Circs', '6082265e-885d-4526-b713-77e59b70691e', '{}'),
(5, '4f99ea18-6559-460e-9693-68f0f5e5bebc', 'USER1', 'ROSH', 1, '2019-11-14 09:00', null, 'More Change of Circs', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '{}');

/* Existing Delius Subject */
insert into subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(6, '362aae3c-852d-4a39-80f4-f41adc249bae', 'DELIUS', '12345', 'John Smith', 'dummy-pnc', 'CRN1', '1928-08-01', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');

INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('fb777be0-a183-4c83-8209-e7871df9c547', 'children_at_risk_of_serious_harm', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);

INSERT INTO question_schema (question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('23c3e984-54c7-480f-b06c-7d000e2fb87c', '203.1', 'child_at_risk_pivot.name_of_child', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null);

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('c093a4ea-46a2-4b98-89cc-6bacaad4d401', '23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 3, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false);