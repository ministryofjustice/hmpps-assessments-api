-- noinspection SqlResolveForFile

delete from assessed_episode where true;
delete from subject where true;
delete from assessment where true;
delete from question_group where true;
delete from oasys_question_mapping where true;
delete from oasys_reference_data_target_mapping where true;
delete from grouping where true;
delete from question_schema where true;

/* Assessment with Episodes */
insert into assessment  (assessment_id, assessment_uuid, created_date) values
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', '2019-11-14 09:00'),
(2, 'bbbae903-7803-4206-800c-2d3b81116d5c', '2019-11-14 09:00'),
(3, 'bd5e5a88-c0ac-4f55-9c08-b8e8bdd9568c', '2019-11-14 09:00'),
(4, '80fd9a2a-59dd-4783-8cac-1689a0464437', '2019-11-14 09:00'),
(5, '8177b6c7-1b20-459b-b6ee-0aeeb2f16857', '2019-11-14 09:00');

insert into subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(1, 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '2e020e78-a81c-407f-bc78-e5f284e237e5'),
(2, 'bf1979c5-518a-4300-80f2-189981182e5f', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', 'bbbae903-7803-4206-800c-2d3b81116d5c'),
(3, 'f0c3c497-b0b8-4fe1-9749-2f686b3b1aa0', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', 'bd5e5a88-c0ac-4f55-9c08-b8e8bdd9568c'),
(4, 'a2bb4345-beba-4806-b719-6cc4ae52ee43', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '80fd9a2a-59dd-4783-8cac-1689a0464437'),
(5, '36afe601-a2d9-4e32-b921-1c20fd0befef', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '8177b6c7-1b20-459b-b6ee-0aeeb2f16857');

insert into assessed_episode  (episode_id, episode_uuid, user_id, created_date, end_date, change_reason, assessment_uuid, answers, oasys_set_pk) values
(1, '8efd9267-e399-48f1-9402-51a08e245f3b', 'USER1', '2019-11-14 09:00', null,'Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}', 1),
(2, 'de1b50ed-90c7-45f5-9dea-5161cec94137', 'USER1', '2019-11-14 09:00', null,'Change of Circs', 'bbbae903-7803-4206-800c-2d3b81116d5c', '{}', 2),
(3, 'b5ade371-1f87-46a1-b784-eb35f6c45e6b', 'USER1', '2019-11-14 09:00', null,'Change of Circs', 'bd5e5a88-c0ac-4f55-9c08-b8e8bdd9568c', '{}', 3),
(4, 'd26658e9-73bf-421c-9de7-a57b602d43e0', 'USER1', '2019-11-14 09:00', null,'Change of Circs', '80fd9a2a-59dd-4783-8cac-1689a0464437', '{}', 4),
(5, '060714ba-dea2-4a1b-bfa6-c07e4934d365', 'USER1', '2019-11-14 09:00', null,'Change of Circs', '8177b6c7-1b20-459b-b6ee-0aeeb2f16857', '{}', 5);

insert into question_schema (question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category) values
('2fe94330-22c4-4a6e-a494-9f53dc2139c6', '30.1', 'oasys_set.originating_division_code', '2019-11-14 08:30', null, 'freetext', null, 'LDU', null, null, null),
('8d48ff4d-60f6-461b-ab00-67d1c2ed5f6b', '31.1', 'oasys_set.originating_team_code', '2019-11-14 08:30', null, 'freetext', null, 'Team', null, null, null);

insert into grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end) values
('1250321c-feff-4b87-83a7-00a65095cab1', 'individual_and_case_details', 'Individual and case details', null, null, '2019-11-14 08:30', null),
('8dc6d75e-7908-4f3b-97d4-48d5441af5e1', 'assessment_details', 'Assessment details', null, null, '2019-11-14 08:30', null);

insert into oasys_question_mapping (mapping_uuid, question_schema_uuid, ref_section_code, logical_page, ref_question_code, fixed_field) values
('b28a7159-edbc-409a-8a53-3d6b8a4ae3b6', '2fe94330-22c4-4a6e-a494-9f53dc2139c6', 'OFFIN', null, 'assessor_office', true),
('1713e728-7738-48d7-8060-3f6f014d6c5c', '8d48ff4d-60f6-461b-ab00-67d1c2ed5f6b', 'OFFIN', null, 'assessor_team', true);

insert into question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only) values
('2d267ba0-5ac5-473f-8369-8ecc424ad0c3', '8dc6d75e-7908-4f3b-97d4-48d5441af5e1', 'group', '1250321c-feff-4b87-83a7-00a65095cab1', 2, true, null, false),
('435a2cbc-a7e9-4ba6-808e-dfe6cd60971f', '2fe94330-22c4-4a6e-a494-9f53dc2139c6', 'question', '8dc6d75e-7908-4f3b-97d4-48d5441af5e1', 6, true, '{"mandatory":{"errorMessage":"select a LDU","errorSummary":"Select a LDU"}}', true),
('47b86036-2a9d-4363-a4a1-fc063a41df4a', '8d48ff4d-60f6-461b-ab00-67d1c2ed5f6b', 'question', '8dc6d75e-7908-4f3b-97d4-48d5441af5e1', 7, true, '{"mandatory":{"errorMessage":"Select a team","errorSummary":"Select a team"}}', true);
