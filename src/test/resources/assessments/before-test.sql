-- noinspection SqlResolveForFile

delete from assessed_episode where true;
delete from subject where true;
delete from assessment where true;

/* Assessment with Episodes */
insert into assessment  (assessment_id, assessment_uuid, supervision_id, created_date) values
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', 'CRN1', '2019-11-14 09:00'),
(2, '19c8d211-68dc-4692-a6e2-d58468127056', null, '2019-11-14 09:00');

insert into subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(1, 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');

insert into assessed_episode  (episode_id, episode_uuid, user_id, created_date, end_date, change_reason, assessment_uuid, answers) values
(1, 'd7aafe55-0cff-4f20-a57a-b66d79eb9c91', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}'),
(2, 'f3569440-efd5-4289-8fdd-4560360e5259', 'USER1', '2019-11-14 09:00', null,'More Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}');

/* Empty assessment */
insert into assessment  (assessment_id, assessment_uuid, supervision_id, created_date) values
(3, 'f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8', 'CRN2', '2020-1-14 09:00');

/* Episodes to complete */
insert into assessment  (assessment_id, assessment_uuid, supervision_id, created_date) values
(4, 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', 'CRN2', '2020-1-14 09:00'),
(5, '6082265e-885d-4526-b713-77e59b70691e', 'CRN2', '2020-1-14 09:00'),
(6, 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', 'CRN2', '2020-1-14 09:00');

insert into subject (subject_id, subject_uuid, source, source_id, name, oasys_offender_pk, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(3, '7bce2323-fefa-42eb-b622-ec65747aae56', 'COURT', 'courtCode|caseNumber2', 'John Smith', 1, 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84'),
(4, '1146f644-dfb9-4e6d-9446-1be089538480', 'COURT', 'courtCode|caseNumber3', 'John Smith', 12345, 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', '6082265e-885d-4526-b713-77e59b70691e'),
(5, 'f6023241-ba22-47e4-bc7d-f7adfde4276c', 'COURT', 'courtCode|caseNumber3', 'John Smith', 5, 'dummy-pnc', 'dummy-crn', '1928-08-01', '2019-11-14 08:30', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5');

insert into assessed_episode  (episode_id, episode_uuid, user_id, assessment_type, oasys_set_pk, created_date, end_date, change_reason, assessment_uuid, answers) values
(3, '163cf020-ff53-4dc6-a15c-e93e8537d347', 'USER1', 0, 1, '2019-11-14 09:00', null, 'More Change of Circs', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '{}'),
(4, '461994f9-86b9-4177-8412-de8dbb18415b', 'USER1', 0, 5678, '2019-11-14 09:00', '2019-11-14 12:00', 'More Change of Circs', '6082265e-885d-4526-b713-77e59b70691e', '{}'),
(5, '4f99ea18-6559-460e-9693-68f0f5e5bebc', 'USER1', 0, 1, '2019-11-14 09:00', null, 'More Change of Circs', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '{}');

/* Existing Delius Subject */
insert into subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, created_date, assessment_uuid) values
(6, '362aae3c-852d-4a39-80f4-f41adc249bae', 'DELIUS', '12345', 'John Smith', 'dummy-pnc', 'CRN1', '1928-08-01', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');
