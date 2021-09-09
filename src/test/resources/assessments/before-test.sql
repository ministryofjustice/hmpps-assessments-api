-- noinspection SqlResolveForFile
DELETE FROM hmppsassessmentsapi.assessed_episode WHERE true;
DELETE FROM hmppsassessmentsapi.subject WHERE true;
DELETE FROM hmppsassessmentsapi.assessment WHERE true;
--
-- DELETE FROM hmppsassessmentsschemas.assessment_predictors WHERE true;
-- DELETE FROM hmppsassessmentsschemas.oasys_question_mapping WHERE true;
-- DELETE FROM hmppsassessmentsschemas.answer_schema WHERE true;
-- DELETE FROM hmppsassessmentsschemas.oasys_reference_data_target_mapping WHERE true;
-- DELETE FROM hmppsassessmentsschemas.predictor_field_mapping WHERE true;
-- DELETE FROM hmppsassessmentsschemas.assessment_predictors WHERE true;
-- DELETE FROM hmppsassessmentsschemas.question_dependency WHERE true;
-- DELETE FROM hmppsassessmentsschemas.question_schema WHERE true;
-- DELETE FROM hmppsassessmentsschemas.answer_schema_group WHERE true;
-- DELETE FROM hmppsassessmentsschemas.question_group WHERE true;
-- DELETE FROM hmppsassessmentsschemas.assessment_schema_groups WHERE true;
-- DELETE FROM hmppsassessmentsschemas.assessment_schema WHERE true;
-- DELETE FROM hmppsassessmentsschemas.answer_schema_group WHERE true;
-- DELETE FROM hmppsassessmentsschemas.question_schema WHERE true;
-- DELETE FROM hmppsassessmentsschemas.predictor_field_mapping WHERE true;
-- DELETE FROM hmppsassessmentsschemas.grouping WHERE true;

/* Assessment with Episodes */
INSERT INTO hmppsassessmentsapi.assessment  (assessment_id, assessment_uuid, created_date) VALUES
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', '2019-11-14 09:00'),
(2, '19c8d211-68dc-4692-a6e2-d58468127056', '2019-11-14 09:00');

INSERT INTO hmppsassessmentsapi.subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, gender, created_date, assessment_uuid) VALUES
(1, 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'COURT', 'courtCode|caseNumber', 'John Smith', 'dummy-pnc', 'DX12340A', '2001-01-01', 'MALE', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');

INSERT INTO hmppsassessmentsapi.assessed_episode  (episode_id, episode_uuid, user_id, created_date, end_date, change_reason, assessment_schema_code, assessment_uuid, answers) VALUES
(1, 'd7aafe55-0cff-4f20-a57a-b66d79eb9c91', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change of Circs', 'ROSH','2e020e78-a81c-407f-bc78-e5f284e237e5', '{}'),
(2, 'f3569440-efd5-4289-8fdd-4560360e5259', 'USER1', '2019-11-14 09:00', null,'More Change of Circs', 'ROSH', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}'),
(3, 'f3569440-efd5-4289-8fdd-4560360e5279', 'USER1', '2019-11-14 09:00', null,'Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{}'),
(4, 'f3569440-efd5-4289-8fdd-4560360e5289', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{}'),
(5, 'f3569440-efd5-4289-8fdd-4560360e5299', 'USER1', '2019-11-14 09:00', '2019-11-14 13:00','More recent Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{"total_sanctions": {"answers": [{"items": ["10"]}]}, "any_sexual_offences": {"answers": [{"items": ["YES"]}]},"current_sexual_offence": {"answers": [{"items": ["YES"]}]},"current_offence_victim_stranger": {"answers": [{"items": ["YES"]}]}, "completed_interview": {"answers": [{"items": ["YES"]}]}, "date_first_sanction": {"answers": [{"items": ["2020-01-01"]}]}, "age_first_conviction": {"answers": [{"items": ["23"]}]}, "earliest_release_date": {"answers": [{"items": ["2021-11-01"]}]}, "total_violent_offences": {"answers": [{"items": ["8"]}]}, "date_current_conviction": {"answers": [{"items": ["2020-12-18"]}]}, "total_non_sexual_offences": {"answers": [{"items": []}]}, "total_sexual_offences_adult": {"answers": [{"items": [5]}]}, "total_sexual_offences_child": {"answers": [{"items": [3]}]}, "most_recent_sexual_offence_date": {"answers": [{"items": ["2020-12-11"]}]}, "total_sexual_offences_child_image": {"answers": [{"items": [2]}]}, "total_non_contact_sexual_offences": {"answers": [{"items": [2]}]}, "binge_drinking": {"answers": [{"items": ["significant problems"]}]}, "previous_arson": {"answers": [{"items": ["YES"]}]}, "use_of_alcohol": {"answers": [{"items": ["significant problems"]}]},"previous_robbery": {"answers": [{"items": ["YES"]}]}, "previous_wounding": {"answers": [{"items": ["YES"]}]}, "impulsivity_issues": {"answers": [{"items": ["some problems"]}]},"previous_kidnapping": {"answers": [{"items": ["YES"]}]},"temper_control_issues": {"answers": [{"items": ["significant problems"]}]}, "unemployed_on_release": {"answers": [{"items": ["not available for work"]}]}, "current_sexual_offence": {"answers": [{"items": ["YES"]}]}, "previous_murder_attempt": {"answers": [{"items": ["YES"]}]}, "previous_offence_weapon": {"answers": [{"items": ["YES"]}]}, "previous_criminal_damage": {"answers": [{"items": ["YES"]}]}, "evidence_domestic_violence": {"answers": [{"items": ["YES"]}]}, "previous_possession_firearm": {"answers": [{"items": ["YES"]}]},"previous_aggravated_burglary": {"answers": [{"items": ["YES"]}]}, "perpetrator_domestic_violence": {"answers": [{"items": ["perpetrator"]}]}, "pro_criminal_attitudes": {"answers": [{"items": ["some problems"]}]}, "current_relationship_with_partner": {"answers": [{"items": ["significant problems"]}]}}');

/* Empty assessment */
INSERT INTO hmppsassessmentsapi.assessment  (assessment_id, assessment_uuid, created_date) VALUES
(3, 'f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8', '2020-1-14 09:00');

/* Episodes to complete */
INSERT INTO hmppsassessmentsapi.assessment  (assessment_id, assessment_uuid, created_date) VALUES
(4, 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '2020-1-14 09:00'),
(5, '6082265e-885d-4526-b713-77e59b70691e', '2020-1-14 09:00'),
(6, 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '2020-1-14 09:00');

INSERT INTO hmppsassessmentsapi.subject (subject_id, subject_uuid, source, source_id, name, oasys_offender_pk, pnc, crn, date_of_birth, gender, created_date, assessment_uuid) VALUES
(3, '7bce2323-fefa-42eb-b622-ec65747aae56', 'COURT', 'courtCode|caseNumber2', 'John Smith', 1, 'dummy-pnc', 'X1345', '2001-01-01', 'MALE', '2019-11-14 08:30', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84'),
(4, '1146f644-dfb9-4e6d-9446-1be089538480', 'COURT', 'courtCode|caseNumber3', 'John Smith', 12345, 'dummy-pnc', 'dummy-crn-1', '1928-08-01', 'MALE', '2019-11-14 08:30', '6082265e-885d-4526-b713-77e59b70691e'),
(5, 'f6023241-ba22-47e4-bc7d-f7adfde4276c', 'COURT', 'courtCode|caseNumber3', 'John Smith', 5, 'dummy-pnc', 'dummy-crn-2', '1928-08-01', 'MALE', '2019-11-14 08:30', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5');

INSERT INTO hmppsassessmentsapi.assessed_episode  (episode_id, episode_uuid, user_id, assessment_schema_code, oasys_set_pk, created_date, end_date, change_reason, assessment_uuid, answers) VALUES
(6, '163cf020-ff53-4dc6-a15c-e93e8537d347', 'USER1', 'RSR', 1, '2021-01-01 00:00', null, 'More Change of Circs', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"],"current_sexual_offence": ["YES"],"current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["significant problems"], "previous_arson": ["YES"], "use_of_alcohol": ["significant problems"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["some problems"],"previous_kidnapping": ["YES"],"temper_control_issues": ["significant problems"], "unemployed_on_release": ["not available for work"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["some problems"], "current_relationship_with_partner": ["significant problems"]}'),
(7, '461994f9-86b9-4177-8412-de8dbb18415b', 'USER1', 'ROSH', 5678, '2019-11-14 09:00', '2019-11-14 12:00', 'More Change of Circs', '6082265e-885d-4526-b713-77e59b70691e', '{}'),
(8, '4f99ea18-6559-460e-9693-68f0f5e5bebc', 'USER1', 'ROSH', 1, '2019-11-14 09:00', null, 'More Change of Circs', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '{}');

/* Existing Delius Subject */
INSERT INTO hmppsassessmentsapi.subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, gender, created_date, assessment_uuid) VALUES
(6, '362aae3c-852d-4a39-80f4-f41adc249bae', 'DELIUS', '12345', 'John Smith', 'dummy-pnc', 'CRN1', '1928-08-01', 'MALE', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');
/*

INSERT INTO hmppsassessmentsschemas.grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('fb777be0-a183-4c83-8209-e7871df9c547', 'children_at_risk_of_serious_harm', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);

INSERT INTO hmppsassessmentsschemas.question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question_code_for_test', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null);

INSERT INTO hmppsassessmentsschemas.question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('c093a4ea-46a2-4b98-89cc-6bacaad4d401', '23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 3, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false);

INSERT INTO hmppsassessmentsschemas.assessment_schema (assessment_schema_id, assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
(0, 'c3a6beac-37c0-46b6-b4b3-62086b624675', 'ROSH', 'SHORT_FORM_PSR', 'START', 'Rosh Assessment')
ON CONFLICT DO NOTHING;
INSERT INTO hmppsassessmentsschemas.assessment_schema (assessment_schema_id, assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
(1, 'c4a6beac-37c0-46b6-b4b3-62086b624675', 'RSR', 'SOMETHING_IN_OASYS', 'START', 'Another Assessment type')
ON CONFLICT DO NOTHING;

INSERT INTO hmppsassessmentsschemas.assessment_predictors (assessment_schema_code, predictor_type) VALUES
('RSR', 'RSR')
ON CONFLICT DO NOTHING;

INSERT INTO hmppsassessmentsschemas.assessment_schema_groups(assessment_schema_group_id, assessment_schema_uuid, group_uuid)
VALUES (0, 'c3a6beac-37c0-46b6-b4b3-62086b624675', 'fb777be0-a183-4c83-8209-e7871df9c547')
ON CONFLICT DO NOTHING;

INSERT INTO hmppsassessmentsschemas.assessment_schema_groups(assessment_schema_group_id, assessment_schema_uuid, group_uuid)
VALUES (1, 'c4a6beac-37c0-46b6-b4b3-62086b624675', 'fb777be0-a183-4c83-8209-e7871df9c547')
ON CONFLICT DO NOTHING;

INSERT INTO hmppsassessmentsschemas.answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES ('887f4528-06d1-4247-8bc3-5e679222baa6', 'yes-no', '2020-11-30 14:50:00', null),
('701e4016-3447-40d2-a9fb-cc5e7490fe70', 'selectananswer-tbc', '2020-11-30 14:50:00', null),
('ee47fa78-eaba-421a-bcaf-0aea184dc5a3', 'bailinformationrecord-basiccustodyscreening-communitypayback-hostel-interview-judgescomments-localauthority-medical-oasyssaq-police-post-trialreport-pre-sentencereport-previousconvictions-previousoasys-prisonrecords-prosecutorinccps-psychiatric-psychology-requestforinformation-victimstatement-other', '2020-11-30 14:50:00', null),
('d03940ce-5f84-4ec1-af45-ab2957d09402', 'noproblems-someproblems-significantproblems', '2020-11-30 14:50:00', null),
('f95ff568-cfa0-4dec-b5f4-f82c950b24d4', 'no-notavailableforwork-yes', '2020-11-30 14:50:00', null),
('f01257fa-c05b-40fc-a134-1fcf6809e9f9', 'victim', '2020-11-30 14:50:00', null),
('b6c049bf-b6a5-42db-af2b-0cab2733dc1b', 'perpetrator', '2020-11-30 14:50:00', null),
('a2a6d156-4a70-41cf-989c-2857b4b2e625', 'yes-no', '2020-11-30 14:50:00', null),
('5eb0380b-f928-481a-a8c4-255821dc69b2', 'yes-no-dontknow', '2020-11-30 14:50:00', null),
('16a910e5-e06f-4b56-937a-65ec8bf492ce', 'yes-no-n', '2020-11-30 14:50:00', null),
('f8a83fb6-b49f-48c8-9044-2d234bcedb62', 'low-medium-high-veryhigh', '2020-11-30 14:50:00', null);

INSERT INTO hmppsassessmentsschemas.question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES
('5ca86a06-5472-4861-bd6a-a011780db49a', 'date_first_sanction', '2020-11-30 14:50:00', null, 'date', null, 'Date of first sanction', null, null, null),
('63099aab-f852-4dd9-9179-16ee2218d0c6', 'age_first_conviction', '2020-11-30 14:50:00', null, 'numeric', null, 'Age at first conviction, conditional or absolute discharge in years', 'Record in years', null, null),
('8e83a0ad-2fcf-4afb-a0af-09d1e23d3c33', 'total_sanctions', '2020-11-30 14:50:00', null, 'numeric', null, 'Total number of sanctions for all offences', null, null, null),
('496587b9-81f3-47ad-a41e-77900fdca573', 'total_violent_offences', '2020-11-30 14:50:00', null, 'numeric', null, 'How many of the total number of sanctions involved violent offences?', null, null, null),
('f5d1dd7c-1774-4c76-89c2-a47240ad98ba', 'date_current_conviction', '2020-11-30 14:50:00', null, 'date', null, 'Date of current conviction', 'For example, 12 11 2007', null, null),
('58d3efd1-65a1-439b-952f-b2826ffa5e71', 'any_sexual_offences', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Have they ever committed a sexual offence?', null, null, null),
('3662710d-ce3e-4e45-bce3-caa4155872aa', 'current_sexual_offence', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the current offence have a sexual motivation?', null, null, null),
('86ee742c-4bfb-4e29-afca-04ad35a3abda', 'current_offence_victim_stranger', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the current offence involve a victim who was a stranger?', null, null, null),
('a00223d0-1c20-43b5-8076-8a292ca25773', 'most_recent_sexual_offence_date', '2020-11-30 14:50:00', null, 'date', null, 'Date of most recent sanction involving a sexual or sexually motivated offence', 'For example, 12 11 2007', null, null),
('fc45b061-a4a6-44c3-937c-2949069e0926', 'total_sexual_offences_adult', '2020-11-30 14:50:00', null, 'numeric', null, 'Number of previous or current sanctions involving contact adult sexual or sexually motivated offences', null, null, null),
('ed495c57-21f3-4388-87e6-57017a6999b1', 'total_sexual_offences_child', '2020-11-30 14:50:00', null, 'numeric', null, 'Number of previous or current sanctions involving contact child sexual or sexually motivated offences', null, null, null),
('00a559e4-32d5-4ae7-aa21-247068a639ad', 'total_sexual_offences_child_image', '2020-11-30 14:50:00', null, 'numeric', null, 'Number of previous or current sanctions involving indecent child image sexual or sexually motivated offences', null, null, null),
('1b6c8f79-0fd9-45d4-ba50-309c3ccfdb2d', 'total_non_contact_sexual_offences', '2020-11-30 14:50:00', null, 'numeric', null, 'Number of previous or current sanctions involving other non-contact sexual or sexually motivated offences', null, null, null),
('5cd344d4-acf3-45a9-9493-5dda5aa9dfa8', 'earliest_release_date', '2020-11-30 14:50:00', null, 'date', null, 'Date of commencement of community sentence or earliest possible release from custody', 'For example, 12 11 2007', null, null),
('420c2ffe-8f2c-49b3-a523-674af3197b9e', 'completed_interview', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Have you completed an interview with the individual?', null, null, null),
('ed0e988a-38a4-4f9f-9691-08fb695cbed9', 'suitable_accommodation', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'Is the individual living in suitable accommodation?', null, null, null),
('3211a668-8878-4e88-8457-8250bfe65aea', 'unemployed_on_release', '2020-11-30 14:50:00', null, 'radio', 'f95ff568-cfa0-4dec-b5f4-f82c950b24d4', 'Is the person unemployed or will be unemployed upon release?', null, null, null),
('1970ba5e-91cb-4ad3-9f04-64d5b5b7157b', 'current_relationship_with_partner', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'What is the person''s current relationship with their partner?', null, null, null),
('f04dd882-0a0d-49f5-9736-91eeadbff9e7', 'evidence_domestic_violence', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Is there evidence that the individual is a perpetrator of domestic abuse?', null, null, null),
('fb98f8b1-58ed-4aac-85b1-404f84270b95', 'victim_domestic_violence', '2020-11-30 14:50:00', null, 'checkbox', 'f01257fa-c05b-40fc-a134-1fcf6809e9f9', 'Victim', null, null, null),
('38b3a40a-df23-4ea1-872e-c04a8b03ee05', 'perpetrator_domestic_violence', '2020-11-30 14:50:00', null, 'checkbox', 'b6c049bf-b6a5-42db-af2b-0cab2733dc1b', 'Perpetrator', null, null, null),
('f0416e89-3a71-46d1-8fa2-aebd886dcb34', 'use_of_alcohol', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'Is the person''s current use of alcohol a problem?', null, null, null),
('574618c3-27f4-4dd2-94bb-6de74126ff22', 'binge_drinking', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'Is there evidence of binge drinking or excessive use of alcohol in the last 6 months?', null, null, null),
('5a90a38d-ee0a-4775-994c-addf3397b817', 'impulsivity_issues', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'Is impulsivity a problem for the individual?', null, null, null),
('d0619e6b-cc90-4031-90c6-ab15e06cc779', 'temper_control_issues', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'Is temper control a problem for the individual?', null, null, null),
('2f1b543b-1e69-4f7e-a61c-6d21ff967432', 'pro_criminal_attitudes', '2020-11-30 14:50:00', null, 'radio', 'd03940ce-5f84-4ec1-af45-ab2957d09402', 'Does the individual have pro-criminal attitudes?', null, null, null),
('4dab51be-e9f8-4ca1-8765-b58e07e137ba', 'complete_risk_assessment', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are you completing a risk assessment?', null, null, null),
('1d46721e-f0d1-436b-ba4f-8e2e8c8b34b0', 'currently_convicted', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Has the individual been currently convicted of any of the following offences?', 'The following fields are mandatory.', null, null),
('39b39e27-3ad3-4e06-a93f-5b5314811379', 'serious_offence', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Any other current or previous offence which is as serious, eg blackmail, harassment, stalking, child pornography, child neglect, abduction etc. Indicate offence below', null, null, null),
('8d717993-3e89-43b2-ac78-b739dccbf643', 'serious_offence_details', '2020-11-30 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('0941c5b2-f42d-4120-ad79-44954674fe00', 'previous_murder_attempt', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Murder/attempted murder/threat or conspiracy to murder/manslaughter', null, null, null),
('f988f76c-3d6c-4f45-aa29-7dc8d11198d7', 'previous_wounding', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Wounding/GBH (Sections 18/20 Offences Against the Person Act 1861)', null, null, null),
('ad81d270-4acc-472c-a79e-01d0a422ce80', 'previous_aggravated_burglary', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Aggravated burglary', null, null, null),
('f8789074-1532-4b32-8995-780da18e273a', 'previous_arson', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Arson', null, null, null),
('df5d635a-6765-42af-9007-6d6d333da5f2', 'previous_criminal_damage', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Criminal damage with intent to endanger life', null, null, null),
('ee2763b4-fff9-42c3-a784-6e6fe6b1dea9', 'previous_sexual_offence_child', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Any sexual offence against a child', null, null, null),
('06d5f00f-a5db-4cc5-b51e-a8c91e63686f', 'previous_sexual_offence_Adult', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Rape or serious sexual offence against an adult', null, null, null),
('0dcc92ff-20d4-4ee1-b3d7-b124b6f32ae8', 'previous_offence_child', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Any other offence against a child', null, null, null),
('c2b221b4-ee1f-41c8-8fc7-9d49998fab35', 'previous_kidnapping', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Kidnapping/false imprisonment', null, null, null),
('97621b0d-5a64-42dd-9c8b-1484979e9145', 'current_possession_firearm', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Possession of a firearm with intent to endanger life or resist arrest', null, null, null),
('e887bcea-91d1-4c50-a25e-4335fa1e6ae5', 'previous_possession_firearm', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Possession of a firearm with intent to endanger life or resist arrest', null, null, null),
('9692659a-778a-436a-bf4e-fe1924638e37', 'previous_robbery', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Robbery', null, null, null),
('66c3bdd2-6f47-4f13-aa7b-39d497d90a89', 'previous_racial_offence', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'A racially motivated or racially aggravated offence', null, null, null),
('ba327713-e580-418d-b45b-09a8a3d1166a', 'current_offence_weapon', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Any other offence involving possession and/or use of weapons', null, null, null),
('68e31f3a-5175-47e2-986b-d722ad78d893', 'previous_offence_weapon', '2020-11-30 14:50:00', null, 'radio', 'a2a6d156-4a70-41cf-989c-2857b4b2e625', 'Any other offence involving possession and/or use of weapons', null, null, null);

INSERT INTO hmppsassessmentsschemas.predictor_field_mapping (predictor_mapping_uuid, question_schema_uuid, predictor_type, predictor_field_name, required) VALUES
('c828f801-667f-48cf-8371-8c19aac28d95', '5ca86a06-5472-4861-bd6a-a011780db49a', 'RSR', 'date_first_sanction', true),
('d4e41828-9f40-4a1c-9a3d-121abe140989', '8e83a0ad-2fcf-4afb-a0af-09d1e23d3c33', 'RSR', 'total_sanctions', true),
('2ec91c12-90eb-4cf6-b17b-3cb35b69f55a', '496587b9-81f3-47ad-a41e-77900fdca573', 'RSR', 'total_violent_offences', true),
('7462816f-12d0-4465-9a4b-00e1b1fb629e', 'f5d1dd7c-1774-4c76-89c2-a47240ad98ba', 'RSR', 'date_current_conviction', true),
('521bff0e-93b1-44e1-a472-867b9bd90820', '58d3efd1-65a1-439b-952f-b2826ffa5e71', 'RSR', 'any_sexual_offences', true),
('a4634b3d-60bb-463d-93f1-25168f84232d', '3662710d-ce3e-4e45-bce3-caa4155872aa', 'RSR', 'current_sexual_offence', true),
('5bfd067f-67d0-419d-b6e9-db06532f5c1e', '86ee742c-4bfb-4e29-afca-04ad35a3abda', 'RSR', 'current_offence_victim_stranger', true),
('31418715-5ea6-422a-923a-d32d03630bc3', 'a00223d0-1c20-43b5-8076-8a292ca25773', 'RSR', 'most_recent_sexual_offence_date', true),
('32072397-d849-4708-8281-9feb00893c9f', 'fc45b061-a4a6-44c3-937c-2949069e0926', 'RSR', 'total_sexual_offences_adult', true),
('131fa3c0-119d-432a-a9ab-41f46006199f', 'ed495c57-21f3-4388-87e6-57017a6999b1', 'RSR', 'total_sexual_offences_child', true),
('cc7190e6-5611-4192-ac3b-17f9e8cb8cc6', '00a559e4-32d5-4ae7-aa21-247068a639ad', 'RSR', 'total_sexual_offences_child_image', true),
('e49598a5-5b10-408b-8fac-a0a29c6a3b83', '1b6c8f79-0fd9-45d4-ba50-309c3ccfdb2d', 'RSR', 'total_non_contact_sexual_offences', true),
('c55c3087-c8b1-497c-929b-c1d107c37234', '5cd344d4-acf3-45a9-9493-5dda5aa9dfa8', 'RSR', 'earliest_release_date', true),
('ef3f6c4a-c4d1-4fbe-82eb-3914b7519629', '420c2ffe-8f2c-49b3-a523-674af3197b9e', 'RSR', 'completed_interview', true),
('61d76f92-e603-4c91-9e75-73e48aca8664', 'ed0e988a-38a4-4f9f-9691-08fb695cbed9', 'RSR', 'suitable_accommodation', false),
('6a37b92f-f316-4e66-bb28-fc1c6d88b79b', '3211a668-8878-4e88-8457-8250bfe65aea', 'RSR', 'unemployed_on_release', false),
('b608c6fd-4b93-4581-886b-f5925f0d9b4d', '1970ba5e-91cb-4ad3-9f04-64d5b5b7157b', 'RSR', 'current_relationship_with_partner', false),
('89926828-7862-4b22-b02e-2e449a76c622', 'f04dd882-0a0d-49f5-9736-91eeadbff9e7', 'RSR', 'evidence_domestic_violence', false),
('ca66afba-46c0-4be4-a23d-306bce15a4fd', '38b3a40a-df23-4ea1-872e-c04a8b03ee05', 'RSR', 'perpetrator_domestic_violence', false),
('a4feacfe-6bda-41d6-806e-872c0b84189e', 'f0416e89-3a71-46d1-8fa2-aebd886dcb34', 'RSR', 'use_of_alcohol', false),
('8dea672f-5264-4973-94f4-9d10f1daf5f6', '574618c3-27f4-4dd2-94bb-6de74126ff22', 'RSR', 'binge_drinking', false),
('0365e911-b1bd-4271-b498-3657eda1c859', '5a90a38d-ee0a-4775-994c-addf3397b817', 'RSR', 'impulsivity_issues', false),
('42d9430a-f004-402c-af37-4b8bf24ef0fb', 'd0619e6b-cc90-4031-90c6-ab15e06cc779', 'RSR', 'temper_control_issues', false),
('c5d29ac7-69bb-49a6-bf3d-9943a0e93303', '2f1b543b-1e69-4f7e-a61c-6d21ff967432', 'RSR', 'pro_criminal_attitudes', false),
('8deaa2c7-a646-4d1f-bb18-30e2289d219c', '0941c5b2-f42d-4120-ad79-44954674fe00', 'RSR', 'previous_murder_attempt', false),
('1a51093d-8a55-4c32-9c01-0eacf6bd7c93', 'f988f76c-3d6c-4f45-aa29-7dc8d11198d7', 'RSR', 'previous_wounding', false),
('afb6afb7-4f48-432f-abf4-693710708534', 'ad81d270-4acc-472c-a79e-01d0a422ce80', 'RSR', 'previous_aggravated_burglary', false),
('524c4792-9aff-4447-bd59-fb3ecd9d80e5', 'f8789074-1532-4b32-8995-780da18e273a', 'RSR', 'previous_arson', false),
('23d67995-183a-48d1-98cc-729441b610d2', 'df5d635a-6765-42af-9007-6d6d333da5f2', 'RSR', 'previous_criminal_damage', false),
('28cc6b11-5653-4bf3-a515-89c6fd59a1c9', 'c2b221b4-ee1f-41c8-8fc7-9d49998fab35', 'RSR', 'previous_kidnapping', false),
('a1c0d8da-52a3-4539-a86e-632aea772c7a', 'e887bcea-91d1-4c50-a25e-4335fa1e6ae5', 'RSR', 'previous_possession_firearm', false),
('a7be30d2-987a-44f1-ac1c-9722dc803f20', '9692659a-778a-436a-bf4e-fe1924638e37', 'RSR', 'previous_robbery', false),
('cf0c913f-c5d5-4b28-a861-ac55b218899f', '68e31f3a-5175-47e2-986b-d722ad78d893', 'RSR', 'previous_offence_weapon', false),
('a954021b-8c43-4792-9431-4c63b7b54e96', '97621b0d-5a64-42dd-9c8b-1484979e9145', 'RSR', 'current_possession_firearm', false),
('94476286-d09b-4f26-8025-6d6e5c828bcf', 'ba327713-e580-418d-b45b-09a8a3d1166a', 'RSR', 'current_offence_weapon', false);*/
