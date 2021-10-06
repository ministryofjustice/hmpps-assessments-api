-- noinspection SqlResolveForFile
DELETE FROM hmppsassessmentsapi.assessed_episode WHERE true;
DELETE FROM hmppsassessmentsapi.offence WHERE true;
DELETE FROM hmppsassessmentsapi.assessment WHERE true;
DELETE FROM hmppsassessmentsapi.subject WHERE true;

/* Assessment with Episodes */
INSERT INTO hmppsassessmentsapi.subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) VALUES
('a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', 'John Smith', 'dummy-pnc', 'X1346', '2001-01-01', 'MALE', '2019-11-14 08:30'),
('a5e74a2c-3f1c-4f83-88b6-dd3ce1b78531', 'Peter Smith', 'dummy-pnc', 'X1349', '2001-01-01', 'MALE', '2019-11-14 08:30'),
('a6e74a2c-3f4c-4f83-88b6-dd3ce1b78531', 'Paul Smith', 'dummy-pnc', 'X1355', '2001-01-01', 'MALE', '2019-11-14 08:30');

INSERT INTO hmppsassessmentsapi.assessment  (assessment_uuid, subject_uuid, created_date) VALUES
('2e020e78-a81c-407f-bc78-e5f284e237e5', 'a6e74a2c-3f4c-4f83-88b6-dd3ce1b78531', '2019-11-14 08:00'),
('19c8d211-68dc-4692-a6e2-d58468127056', 'a4e73a2c-3f1c-4f83-88b6-dd3ce1b78530', '2019-11-14 09:00'),
('29c8d211-68dc-4692-a6e2-d58468127356', 'a5e74a2c-3f1c-4f83-88b6-dd3ce1b78531', '2021-01-01 00:00'),
('49c8d211-68dc-4692-a6e2-d58468127356', 'a6e74a2c-3f4c-4f83-88b6-dd3ce1b78531', '2019-11-14 09:00');

INSERT INTO hmppsassessmentsapi.offence (offence_uuid, source, source_id, offence_code, code_description, offence_subcode, subcode_description)
VALUES
('111323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '046', 'Stealing from shops and stalls (shoplifting)', '00', 'Stealing from shops and stalls (shoplifting)'),
('222323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '047', 'Arson', '01', 'Arson'),
('333323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '048', 'Sexual offence', '02', 'Sexual offence'),
('444323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '049', 'Sexual assault', '03', 'Sexual assault'),
('555323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '050', 'Burglary', '04', 'Burglary'),
('666323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '051', 'Murder attempt', '05', 'Murder attempt'),
('777323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '052', 'Children offence', '07', 'Children offence'),
('888323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '053', 'Offence with knife', '08', 'Offence with knife'),
('999323d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '054', 'Offence with weapon', '09', 'Offence with weapon'),
('111111d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '054', 'Offence with weapon', '09', 'Offence with weapon'),
('222222d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '054', 'Offence with weapon', '09', 'Offence with weapon'),
('333333d1-1e0f-42f3-b5b5-f44b0e5bcb18', 'DELIUS', 1, '054', 'Offence with weapon', '09', 'Offence with weapon');

INSERT INTO hmppsassessmentsapi.assessed_episode  (episode_uuid, user_id, created_date, end_date, change_reason, assessment_schema_code, assessment_uuid, answers, offence_uuid) VALUES
('d7aafe55-0cff-4f20-a57a-b66d79eb9c91', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change of Circs', 'ROSH','2e020e78-a81c-407f-bc78-e5f284e237e5', '{}', '111323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f3569440-efd5-4289-8fdd-4560360e5259', 'USER1', '2019-11-14 09:00', null,'More Change of Circs', 'ROSH', '2e020e78-a81c-407f-bc78-e5f284e237e5', '{}', '222323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f3569440-efd5-4289-8fdd-4560360e5279', 'USER1', '2019-11-14 09:00', null,'Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{}', '333323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f3569440-efd5-4289-8fdd-4560360e5289', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{}', '444323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f3569440-efd5-4289-8fdd-4560360e5299', 'USER1', '2019-11-14 09:00', '2019-11-14 13:00','More recent Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"],"current_sexual_offence": ["YES"],"current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["SIGNIFICANT_PROBLEMS"], "previous_arson": ["YES"], "use_of_alcohol": ["SIGNIFICANT_PROBLEMS"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["SOME_PROBLEMS"],"previous_kidnapping": ["YES"],"temper_control_issues": ["SIGNIFICANT_PROBLEMS"], "unemployed_on_release": ["NOT_AVAILABLE_FOR_WORK"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["SOME_PROBLEMS"], "current_relationship_with_partner": ["SIGNIFICANT_PROBLEMS"]}', '555323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f4569450-efd5-4289-8fdd-4560360e5289', 'USER1', '2019-11-14 09:00', null,'Change', 'RSR', '29c8d211-68dc-4692-a6e2-d58468127356', '{}', '666323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f4567470-efd5-4289-8fdd-4570360e5289', 'USER1', '2021-01-01 00:00', null,'Change', 'RSR', '29c8d211-68dc-4692-a6e2-d58468127356', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"], "current_sexual_offence": ["YES"], "current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["SIGNIFICANT_PROBLEMS"], "previous_arson": ["YES"], "use_of_alcohol": ["SIGNIFICANT_PROBLEMS"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["SOME_PROBLEMS"],"previous_kidnapping": ["YES"],"temper_control_issues": ["SIGNIFICANT_PROBLEMS"], "unemployed_on_release": ["NOT_AVAILABLE_FOR_WORK"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "current_offence_weapon": ["YES"], "current_possession_firearm": ["YES"],  "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["SOME_PROBLEMS"], "current_relationship_with_partner": ["SIGNIFICANT_PROBLEMS"]}', '777323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f5565470-efd5-4589-8fdd-4570360e5289', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change', 'RSR', '49c8d211-68dc-4692-a6e2-d58468127356', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"],"current_sexual_offence": ["YES"],"current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["SIGNIFICANT_PROBLEMS"], "previous_arson": ["YES"], "use_of_alcohol": ["SIGNIFICANT_PROBLEMS"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["SOME_PROBLEMS"],"previous_kidnapping": ["YES"],"temper_control_issues": ["SIGNIFICANT_PROBLEMS"], "unemployed_on_release": ["NOT_AVAILABLE_FOR_WORK"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "current_offence_weapon": ["YES"], "current_possession_firearm": ["YES"],  "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["SOME_PROBLEMS"], "current_relationship_with_partner": ["SIGNIFICANT_PROBLEMS"]}', '888323d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('f7765470-efd5-4589-8fdd-4570360e5289', 'USER1', '2019-11-14 09:00', '2019-11-14 13:00','Change', 'RSR', '49c8d211-68dc-4692-a6e2-d58468127356', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"],"current_sexual_offence": ["YES"],"current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["SIGNIFICANT_PROBLEMS"], "previous_arson": ["YES"], "use_of_alcohol": ["SIGNIFICANT_PROBLEMS"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["SOME_PROBLEMS"],"previous_kidnapping": ["YES"],"temper_control_issues": ["SIGNIFICANT_PROBLEMS"], "unemployed_on_release": ["NOT_AVAILABLE_FOR_WORK"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "current_offence_weapon": ["YES"], "current_possession_firearm": ["YES"],  "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["SOME_PROBLEMS"], "current_relationship_with_partner": ["SIGNIFICANT_PROBLEMS"]}', '999323d1-1e0f-42f3-b5b5-f44b0e5bcb18');

/* Empty assessment */
INSERT INTO hmppsassessmentsapi.subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) VALUES
('a5e75a5c-5f1c-5f83-55b6-dd5ce1b75530', 'John Smith', 'dummy-pnc', 'X1348', '2001-01-01', 'MALE', '2019-11-14 08:30');

INSERT INTO hmppsassessmentsapi.assessment  (assessment_uuid, subject_uuid, created_date) VALUES
('f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8', 'a5e75a5c-5f1c-5f83-55b6-dd5ce1b75530', '2020-1-14 09:00');

/* Invalid LAO rules assessment */
INSERT INTO hmppsassessmentsapi.subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) VALUES
('f9d551b4-602e-4dfa-bf4d-32d67a3914af', 'John Smith', 'dummy-pnc', 'OX1348', '2001-01-01', 'MALE', '2019-11-14 08:30');

INSERT INTO hmppsassessmentsapi.assessment  (assessment_uuid, subject_uuid, created_date) VALUES
('6e60784e-584e-4762-952d-d7288e31d4f4', 'f9d551b4-602e-4dfa-bf4d-32d67a3914af', '2020-1-14 09:00');

INSERT INTO hmppsassessmentsapi.assessed_episode  (episode_uuid, user_id, assessment_schema_code, oasys_set_pk, created_date, end_date, change_reason, assessment_uuid, answers, offence_uuid) VALUES
('3df6172f-a931-4fb9-a595-46868893b4ed', 'USER1', 'RSR', 1, '2021-01-01 00:00', null, 'More Change of Circs', '6e60784e-584e-4762-952d-d7288e31d4f4', '{}', '111111d1-1e0f-42f3-b5b5-f44b0e5bcb18');


/* Episodes to complete */
INSERT INTO hmppsassessmentsapi.subject (subject_uuid, name, oasys_offender_pk, pnc, crn, date_of_birth, gender, created_date) VALUES
('7bce2323-fefa-42eb-b622-ec65747aae56', 'John Smith', 1, 'dummy-pnc', 'X1345', '2001-01-01', 'MALE', '2019-11-14 08:30'),
('1146f644-dfb9-4e6d-9446-1be089538480', 'John Smith', 12345, 'dummy-pnc', 'X134698', '1928-08-01', 'MALE', '2019-11-14 08:30'),
('f6023241-ba22-47e4-bc7d-f7adfde4276c', 'John Smith', 5, 'dummy-pnc', 'X134699', '1928-08-01', 'MALE', '2019-11-14 08:30');

INSERT INTO hmppsassessmentsapi.assessment  (assessment_uuid, subject_uuid, created_date) VALUES
('e399ed1b-0e77-4c68-8bbc-d2f0befece84', '7bce2323-fefa-42eb-b622-ec65747aae56', '2020-1-14 09:00'),
('6082265e-885d-4526-b713-77e59b70691e', '1146f644-dfb9-4e6d-9446-1be089538480', '2020-1-14 09:00'),
('aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', 'f6023241-ba22-47e4-bc7d-f7adfde4276c', '2020-1-14 09:00');

INSERT INTO hmppsassessmentsapi.assessed_episode  (episode_uuid, user_id, assessment_schema_code, oasys_set_pk, created_date, end_date, change_reason, assessment_uuid, answers, offence_uuid) VALUES
('163cf020-ff53-4dc6-a15c-e93e8537d347', 'USER1', 'RSR', 1, '2021-01-01 00:00', null, 'More Change of Circs', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"],"current_sexual_offence": ["YES"],"current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["SIGNIFICANT_PROBLEMS"], "previous_arson": ["YES"], "use_of_alcohol": ["SIGNIFICANT_PROBLEMS"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["SOME_PROBLEMS"],"previous_kidnapping": ["YES"],"temper_control_issues": ["SIGNIFICANT_PROBLEMS"], "unemployed_on_release": ["NOT_AVAILABLE_FOR_WORK"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["SOME_PROBLEMS"], "current_relationship_with_partner": ["SIGNIFICANT_PROBLEMS"]}', '111111d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('461994f9-86b9-4177-8412-de8dbb18415b', 'USER1', 'ROSH', 5678, '2019-11-14 09:00', '2019-11-14 12:00', 'More Change of Circs', '6082265e-885d-4526-b713-77e59b70691e', '{}', '222222d1-1e0f-42f3-b5b5-f44b0e5bcb18'),
('4f99ea18-6559-460e-9693-68f0f5e5bebc', 'USER1', 'ROSH', 1, '2019-11-14 09:00', null, 'More Change of Circs', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '{}', '333333d1-1e0f-42f3-b5b5-f44b0e5bcb18');

/* Existing Delius Subject */
INSERT INTO hmppsassessmentsapi.subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) VALUES
('362aae3c-852d-4a39-80f4-f41adc249bae', 'John Smith', 'dummy-pnc', 'CRN1', '1928-08-01', 'MALE', '2019-11-14 08:30');

INSERT INTO hmppsassessmentsschemas.grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('fb777be0-a183-4c83-8209-e7871df9c547', 'children_at_risk_of_serious_harm_test', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);

INSERT INTO hmppsassessmentsschemas.question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question_code_for_test', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null);

INSERT INTO hmppsassessmentsschemas.question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('c093a4ea-46a2-4b98-89cc-6bacaad4d401', '23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 3, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false);

/* Existing Court Subject */
INSERT INTO hmppsassessmentsapi.subject (subject_uuid, name, pnc, crn, date_of_birth, gender, created_date) VALUES
('85a1a9de-9afe-4b49-97b1-0d37fd89eee5', 'John Smith', 'dummy-pnc', 'DX12340A', '1928-08-01', 'MALE', '2019-11-14 08:30'),
('65a6a6de-9afe-4b49-97b1-0d37fd89eee5', 'Martin Smith', 'dummy-pnc', 'DX12440A', '1928-08-01', 'MALE', '2019-11-14 08:30');

INSERT INTO hmppsassessmentsapi.assessment  (assessment_uuid, subject_uuid, created_date) VALUES
('1e010e18-a11c-107f-bc78-e5f284e237e5', '65a6a6de-9afe-4b49-97b1-0d37fd89eee5', '2019-11-14 09:00'),
('2e020e28-a21c-207f-bc78-e5f284e237e5', '85a1a9de-9afe-4b49-97b1-0d37fd89eee5', '2019-11-14 09:00'),
('4e040e44-a21c-207f-bc48-e5f284e237e5', '362aae3c-852d-4a39-80f4-f41adc249bae', '2019-11-14 09:00');