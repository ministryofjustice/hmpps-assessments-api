-- noinspection SqlResolveForFile
set schema hmppsassessmentsapi;

delete from assessed_episode where true;
delete from subject where true;
delete from assessment where true;

/* Assessment with Court Subject */
insert into assessment  (assessment_id, assessment_uuid, created_date) values
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', '2019-11-14 09:00'),
(2, '19c8d211-68dc-4692-a6e2-d58468127056', '2019-11-14 09:00');

insert into subject (subject_id, subject_uuid, name, pnc, crn, date_of_birth, gender, created_date, assessment_uuid) values
(1, 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'John Smith', 'dummy-pnc', 'dummy-crn', '1928-08-01', 'MALE', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056'),
(2, '8a16598c-e175-417e-afd4-4d2b4cf4313e', 'John Smith', 'dummy-pnc', 'dummy-crn2', '1928-08-01', 'MALE', '2020-11-14 08:30', '2e020e78-a81c-407f-bc78-e5f284e237e5'),
(3, '3d224d7e-c29c-44d7-86d5-0816764889ee', 'John Smith', 'dummy-pnc', 'DX12340A', '1928-08-01', 'MALE', '2021-1-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');