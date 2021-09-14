-- noinspection SqlResolveForFile
DELETE FROM hmppsassessmentsapi.assessed_episode WHERE true;
DELETE FROM hmppsassessmentsapi.subject WHERE true;
DELETE FROM hmppsassessmentsapi.assessment WHERE true;

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
(5, 'f3569440-efd5-4289-8fdd-4560360e5299', 'USER1', '2019-11-14 09:00', '2019-11-14 13:00','More recent Change', 'RSR', '19c8d211-68dc-4692-a6e2-d58468127056', '{"total_sanctions": {"answers": [{"items": ["10"]}]}, "any_sexual_offences": {"answers": [{"items": ["YES"]}]},"current_sexual_offence": {"answers": [{"items": ["YES"]}]},"current_offence_victim_stranger": {"answers": [{"items": ["YES"]}]}, "completed_interview": {"answers": [{"items": ["YES"]}]}, "date_first_sanction": {"answers": [{"items": ["2020-01-01"]}]}, "age_first_conviction": {"answers": [{"items": ["23"]}]}, "earliest_release_date": {"answers": [{"items": ["2021-11-01"]}]}, "total_violent_offences": {"answers": [{"items": ["8"]}]}, "date_current_conviction": {"answers": [{"items": ["2020-12-18"]}]}, "total_non_sexual_offences": {"answers": [{"items": []}]}, "total_sexual_offences_adult": {"answers": [{"items": [5]}]}, "total_sexual_offences_child": {"answers": [{"items": [3]}]}, "most_recent_sexual_offence_date": {"answers": [{"items": ["2020-12-11"]}]}, "total_sexual_offences_child_image": {"answers": [{"items": [2]}]}, "total_non_contact_sexual_offences": {"answers": [{"items": [2]}]}, "binge_drinking": {"answers": [{"items": ["SIGNIFICANT_PROBLEMS"]}]}, "previous_arson": {"answers": [{"items": ["YES"]}]}, "use_of_alcohol": {"answers": [{"items": ["SIGNIFICANT_PROBLEMS"]}]},"previous_robbery": {"answers": [{"items": ["YES"]}]}, "previous_wounding": {"answers": [{"items": ["YES"]}]}, "impulsivity_issues": {"answers": [{"items": ["SOME_PROBLEMS"]}]},"previous_kidnapping": {"answers": [{"items": ["YES"]}]},"temper_control_issues": {"answers": [{"items": ["SIGNIFICANT_PROBLEMS"]}]}, "unemployed_on_release": {"answers": [{"items": ["NOT_AVAILABLE_FOR_WORK"]}]}, "current_sexual_offence": {"answers": [{"items": ["YES"]}]}, "previous_murder_attempt": {"answers": [{"items": ["YES"]}]}, "previous_offence_weapon": {"answers": [{"items": ["YES"]}]}, "previous_criminal_damage": {"answers": [{"items": ["YES"]}]}, "evidence_domestic_violence": {"answers": [{"items": ["YES"]}]}, "previous_possession_firearm": {"answers": [{"items": ["YES"]}]},"previous_aggravated_burglary": {"answers": [{"items": ["YES"]}]}, "perpetrator_domestic_violence": {"answers": [{"items": ["perpetrator"]}]}, "pro_criminal_attitudes": {"answers": [{"items": ["SOME_PROBLEMS"]}]}, "current_relationship_with_partner": {"answers": [{"items": ["SIGNIFICANT_PROBLEMS"]}]}}');

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
(6, '163cf020-ff53-4dc6-a15c-e93e8537d347', 'USER1', 'RSR', 1, '2021-01-01 00:00', null, 'More Change of Circs', 'e399ed1b-0e77-4c68-8bbc-d2f0befece84', '{"total_sanctions": ["10"], "any_sexual_offences": ["YES"],"current_sexual_offence": ["YES"],"current_offence_victim_stranger": ["YES"], "completed_interview": ["YES"], "date_first_sanction": ["2020-01-01"], "age_first_conviction": ["23"], "earliest_release_date": ["2021-11-01"], "total_violent_offences": ["8"], "date_current_conviction": ["2020-12-18"], "total_non_sexual_offences": [], "total_sexual_offences_adult": [5], "total_sexual_offences_child": [3], "most_recent_sexual_offence_date": ["2020-12-11"], "total_sexual_offences_child_image": [2], "total_non_contact_sexual_offences": [2], "binge_drinking": ["SIGNIFICANT_PROBLEMS"], "previous_arson": ["YES"], "use_of_alcohol": ["SIGNIFICANT_PROBLEMS"],"previous_robbery": ["YES"], "previous_wounding": ["YES"], "impulsivity_issues": ["SOME_PROBLEMS"],"previous_kidnapping": ["YES"],"temper_control_issues": ["SIGNIFICANT_PROBLEMS"], "unemployed_on_release": ["NOT_AVAILABLE_FOR_WORK"], "current_sexual_offence": ["YES"], "previous_murder_attempt": ["YES"], "previous_offence_weapon": ["YES"], "previous_criminal_damage": ["YES"], "evidence_domestic_violence": ["YES"], "previous_possession_firearm": ["YES"],"previous_aggravated_burglary": ["YES"], "perpetrator_domestic_violence": ["perpetrator"], "pro_criminal_attitudes": ["SOME_PROBLEMS"], "current_relationship_with_partner": ["SIGNIFICANT_PROBLEMS"]}'),
(7, '461994f9-86b9-4177-8412-de8dbb18415b', 'USER1', 'ROSH', 5678, '2019-11-14 09:00', '2019-11-14 12:00', 'More Change of Circs', '6082265e-885d-4526-b713-77e59b70691e', '{}'),
(8, '4f99ea18-6559-460e-9693-68f0f5e5bebc', 'USER1', 'ROSH', 1, '2019-11-14 09:00', null, 'More Change of Circs', 'aa47e6c4-e41f-467c-95e7-fcf5ffd422f5', '{}');

/* Existing Delius Subject */
INSERT INTO hmppsassessmentsapi.subject (subject_id, subject_uuid, source, source_id, name, pnc, crn, date_of_birth, gender, created_date, assessment_uuid) VALUES
(6, '362aae3c-852d-4a39-80f4-f41adc249bae', 'DELIUS', '12345', 'John Smith', 'dummy-pnc', 'CRN1', '1928-08-01', 'MALE', '2019-11-14 08:30', '19c8d211-68dc-4692-a6e2-d58468127056');

INSERT INTO hmppsassessmentsschemas.grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('fb777be0-a183-4c83-8209-e7871df9c547', 'children_at_risk_of_serious_harm_test', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);

INSERT INTO hmppsassessmentsschemas.question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question_code_for_test', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null);

INSERT INTO hmppsassessmentsschemas.question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('c093a4ea-46a2-4b98-89cc-6bacaad4d401', '23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 3, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false);