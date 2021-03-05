
INSERT INTO assessment (assessment_uuid, supervision_id, created_date, completed_date )
VALUES ('db9dcd12-2e47-4b00-b8e9-44ddf38b9d6a', '12345', '2019-11-14 08:11:53.177108', null);


INSERT INTO subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, created_date, assessment_uuid) VALUES
(1, 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'COURT', 'B10JQ|160346204', 'Arthur Morgan', '2004/0046583U', 'X346204', '1975-01-01', '2019-11-14 08:30', 'db9dcd12-2e47-4b00-b8e9-44ddf38b9d6a');