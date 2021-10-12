SET search_path TO hmppsassessmentsapi;

insert into subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date, oasys_offender_pk) values
('fac28f68-0012-46b9-8de8-6ff2bdbe1c22', 'Gary Hart', null, 'X259950', '1977-08-15', 'MALE', '2021-07-12 16:42:06', 7308807)
    ON CONFLICT DO NOTHING;

INSERT INTO assessment (assessment_uuid, subject_uuid, created_date, completed_date )
VALUES ('fb6b7c33-07fc-4c4c-a009-8d60f66952c4', 'fac28f68-0012-46b9-8de8-6ff2bdbe1c22', '2021-01-01 08:11:53.177108', null)
ON CONFLICT DO NOTHING;

INSERT INTO offence (offence_uuid, source, source_id, offence_code, code_description, offence_subcode, subcode_description, sentence_date)
VALUES ('877323d1-1e0f-42f3-b5b5-f44b0e5bcb88', 'DELIUS', 1, '046', 'Stealing from shops and stalls (shoplifting)', '00', 'Stealing from shops and stalls (shoplifting)', '2021-07-12')
ON CONFLICT DO NOTHING;

INSERT INTO author (author_uuid, user_id, user_source, user_name, user_full_name )
VALUES ('56e16a74-d373-4695-915f-16b6596d594b', '128878', 'delius' ,'AALONSO', 'A Alonso')
ON CONFLICT DO NOTHING;

INSERT INTO assessed_episode (episode_uuid, author_uuid, created_date, end_date, change_reason, assessment_schema_code, assessment_uuid, answers, tables, oasys_set_pk, offence_uuid )
VALUES ('7231b3e7-f002-4a53-b398-fcd34c1d3e97', '56e16a74-d373-4695-915f-16b6596d594b', '2021-01-01 08:11:53.177109', null, 'new episode', 'ROSH', 'fb6b7c33-07fc-4c4c-a009-8d60f66952c4','{}','{}', 9519347, '877323d1-1e0f-42f3-b5b5-f44b0e5bcb88')
ON CONFLICT DO NOTHING;

-- RSR assessment
insert into subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) values
('087bdec0-98de-4c86-af3d-2d05ff978007', 'Gary Hart', null, 'X259951', '1977-08-15', 'MALE', '2021-07-12 16:42:06')
ON CONFLICT DO NOTHING;

INSERT INTO assessment (assessment_uuid, subject_uuid, created_date, completed_date )
VALUES ('6f3f2c4a-38ac-49ce-b790-70bc170fe553', '087bdec0-98de-4c86-af3d-2d05ff978007', '2021-01-01 08:11:53.177108', null)
ON CONFLICT DO NOTHING;

INSERT INTO offence (offence_uuid, source, source_id, offence_code, code_description, offence_subcode, subcode_description, sentence_date)
VALUES ('977323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '046', 'Stealing from shops and stalls (shoplifting)', '00', 'Stealing from shops and stalls (shoplifting)', '2021-07-12')
ON CONFLICT DO NOTHING;

INSERT INTO assessed_episode (episode_uuid, author_uuid, created_date, end_date, change_reason, assessment_schema_code, assessment_uuid, answers, tables, offence_uuid)
VALUES ('90f2b674-ae1c-488d-8b85-0251708ef6b6', '56e16a74-d373-4695-915f-16b6596d594b', '2021-01-01 08:11:53.177109', null, 'new episode', 'RSR', '6f3f2c4a-38ac-49ce-b790-70bc170fe553','{}','{}', '977323d1-1e0f-42f3-b5b5-f44b0e5bcb18')
ON CONFLICT DO NOTHING;
