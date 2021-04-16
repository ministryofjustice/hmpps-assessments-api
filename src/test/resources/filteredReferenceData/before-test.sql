-- noinspection SqlResolveForFile

delete from assessed_episode where true;
delete from subject where true;
delete from assessment where true;

/* Assessment with Episodes */
insert into assessment  (assessment_id, assessment_uuid, supervision_id, created_date) values
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', 'CRN1', '2019-11-14 09:00'),
(2, 'bbbae903-7803-4206-800c-2d3b81116d5c', 'CRN1', '2019-11-14 09:00'),
(3, 'bd5e5a88-c0ac-4f55-9c08-b8e8bdd9568c', 'CRN1', '2019-11-14 09:00'),
(4, '80fd9a2a-59dd-4783-8cac-1689a0464437', 'CRN1', '2019-11-14 09:00'),
(5, '8177b6c7-1b20-459b-b6ee-0aeeb2f16857', 'CRN1', '2019-11-14 09:00');

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
