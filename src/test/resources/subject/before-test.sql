-- noinspection SqlResolveForFile
set schema hmppsassessmentsapi;

DELETE FROM assessed_episode WHERE true;
DELETE FROM author WHERE true;
DELETE FROM offence WHERE true;
DELETE FROM assessment WHERE true;
DELETE FROM subject WHERE true;

insert into subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) values
('a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'John Smith', 'dummy-pnc', 'dummy-crn-1', '1928-08-01', 'MALE', '2019-11-14 08:30'),
('8a16598c-e175-417e-afd4-4d2b4cf4313e', 'John Smith', 'dummy-pnc', 'dummy-crn-2', '1928-08-01', 'MALE', '2020-11-14 08:30'),
('3d224d7e-c29c-44d7-86d5-0816764889ee', 'John Smith', 'dummy-pnc', 'DX12340A', '1928-08-01', 'MALE', '2021-1-14 08:30');
