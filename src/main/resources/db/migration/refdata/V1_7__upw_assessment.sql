INSERT INTO assessment_schema (assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
('9c318330-091a-4a97-842f-1146df9e9703', 'UPW', 'SOMETHING_IN_OASYS', 'END', 'Unpaid Work Assessment');

INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 'upw_assessment', 'Unpaid Work Assessment', null, null, '2021-09-27 14:50:00', null),
       ('1255f7c4-81fe-494c-b269-38f7261cb68c', 'upw_risk_of_harm_community', 'Risk of harm in the community', null, null, '2021-09-27 14:50:00', null);

INSERT INTO assessment_schema_groups(assessment_schema_uuid, group_uuid)
VALUES ('9c318330-091a-4a97-842f-1146df9e9703', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4');


INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES
('30851ffd-90a3-430d-8239-0386076de177', 'violent_offences', '2021-09-27 14:50:00', null, 'freetext', null, 'Violent offences?', null, null, null),
('6d18c3e9-a921-4b3b-aad3-a1c1f68d51c9', 'frequent_dishonesty', '2021-09-27 14:50:00', null, 'freetext', null, 'Frequent Dishonesty?', null, null, null),
('2ef3e974-245e-485a-9dfb-2107fdde53a7', 'gang_identifier', '2021-09-27 14:50:00', null, 'freetext', null, 'Gang identifier, name, or location?', null, null, null),
('832e7462-f9db-4b01-832c-71ad21e5d70a', 'control_issues', '2021-09-27 14:50:00', null, 'freetext', null, 'Control issues or disruptive behaviour?', null, null, null),
('b446b9db-d57a-4a29-9674-37099bdf1a00', 'hate_based_behaviour', '2021-09-27 14:50:00', null, 'freetext', null, 'Hate-based behaviour?', 'For example, homophobic or racially motivated', null, null),
('cc19e283-3ece-4f4b-af2f-c9f2b71e4947', 'high_profile_person', '2021-09-27 14:50:00', null, 'freetext', null, 'High profile person, including on social media, or is vulnerable on a publicly visible project?', 'For example, homophobic or racially motivated', null, null),
;

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES
('85ede91e-95bb-4b47-b732-4b05d794a041', '1255f7c4-81fe-494c-b269-38f7261cb68c', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 1, true, null, false),
('2c750777-45d0-4b96-8715-c8c20f4aa563', '30851ffd-90a3-430d-8239-0386076de177', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 1, true, null, false);
