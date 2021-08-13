INSERT INTO assessment (assessment_uuid, created_date, completed_date )
VALUES ('fb6b7c33-07fc-4c4c-a009-8d60f66952c4', '2019-11-14 08:11:53.177108', null)
ON CONFLICT DO NOTHING;

insert into subject (subject_uuid, source, source_id, name, pnc, crn, date_of_birth, gender, created_date, assessment_uuid, OASYS_OFFENDER_PK) values
('fac28f68-0012-46b9-8de8-6ff2bdbe1c22', 'DELIUS', 1, 'EleRgvLL JanRgvDD', null, 'X259950', '1977-08-15', 'MALE', '2021-07-12 16:42:06', 'fb6b7c33-07fc-4c4c-a009-8d60f66952c4', 7308807)
ON CONFLICT DO NOTHING;

INSERT INTO assessed_episode (episode_uuid, user_id, created_date, end_date, change_reason, assessment_schema_code, assessment_uuid, answers, OASYS_SET_PK  )
VALUES ('7231b3e7-f002-4a53-b398-fcd34c1d3e97', 'AALONSO', '2019-11-14 08:11:53.177108', null, 'new episode', 'ROSH', 'fb6b7c33-07fc-4c4c-a009-8d60f66952c4','{}', 9519347)
ON CONFLICT DO NOTHING;


-- RSR assessment
INSERT INTO assessment (assessment_uuid, created_date, completed_date )
VALUES ('6f3f2c4a-38ac-49ce-b790-70bc170fe553', '2021-01-01 08:11:53.177108', null)
ON CONFLICT DO NOTHING;

insert into subject (subject_uuid, source, source_id, name, pnc, crn, date_of_birth, gender, created_date, assessment_uuid) values
('087bdec0-98de-4c86-af3d-2d05ff978007', 'DELIUS', 1, 'EleRgvLL JanRgvDD', null, 'X259950', '1977-08-15', 'MALE', '2021-07-12 16:42:06', '6f3f2c4a-38ac-49ce-b790-70bc170fe553')
ON CONFLICT DO NOTHING;

INSERT INTO assessed_episode (episode_uuid, user_id, created_date, end_date, change_reason, assessment_schema_code, assessment_uuid, answers  )
VALUES ('90f2b674-ae1c-488d-8b85-0251708ef6b6', 'AALONSO', '2019-11-14 08:11:53.177108', null, 'new episode', 'RSR', '6f3f2c4a-38ac-49ce-b790-70bc170fe553','{}')
ON CONFLICT DO NOTHING;