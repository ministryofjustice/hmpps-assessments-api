INSERT INTO assessment_schema (assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
('9c318330-091a-4a97-842f-1146df9e9703', 'UPW', 'SOMETHING_IN_OASYS', 'END', 'Unpaid Work Assessment');

INSERT INTO answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES
('8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'yes-noillcomebacklater', '2021-09-27 14:50:00', null),
('2a82f463-97d1-4de4-8601-23fca9215c17', 'individual-mixed-femaleonly', '2021-09-27 14:50:00', null);

INSERT INTO answer_schema (answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES
('51c9db72-9c69-4377-818c-b107572eab33', 'yes', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', '2021-09-27 14:50:00', null, 'YES', 'Yes'),
('70ca6d84-730d-45b2-b531-1d8d13fefadc', 'no_ill_come_back_later', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', '2021-09-27 14:50:00', null, 'NO_ILL_COME_BACK_LATER', 'No, I’ll come back later'),
('e8689a6c-2b53-4d6c-b9bf-3bbb0b4a6f85', 'individual', '2a82f463-97d1-4de4-8601-23fca9215c17', '2021-09-27 14:50:00', null, 'INDIVIDUAL', 'Individual'),
('dfaaa874-928b-4bd8-987a-5cdac16e8dba', 'mixed_group', '2a82f463-97d1-4de4-8601-23fca9215c17', '2021-09-27 14:50:00', null, 'MIXED_GROUP', 'Mixed group'),
('66646d01-19a5-4b9f-bfaf-a2f162897336', 'female_only_group', '2a82f463-97d1-4de4-8601-23fca9215c17', '2021-09-27 14:50:00', null, 'FEMALE_ONLY_GROUP', 'Female only group')
;

INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 'upw_assessment', 'Unpaid Work Assessment', null, null, '2021-09-27 14:50:00', null),
       ('2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 'upw_diversity_section', 'Risk section', null, null, '2021-09-27 14:50:00', null),
       ('667e9967-275f-4d23-bd02-7b5e3f3e1647', 'upw_cultural_info', 'Cultural or Religious adjustments', null, null, '2021-09-27 14:50:00', null),
       ('d633d6e1-e252-4c09-a21c-c8cc558bce12', 'upw_placement_preferences', 'Placement preferences', null, null, '2021-09-27 14:50:00', null),
       ('b9114d94-2500-456e-8d2e-777703dfd6bc', 'upw_placement_gender_preferences', 'Placement preferences based on gender identity', null, null, '2021-09-27 14:50:00', null),
       ('b7b11bf3-836b-4cba-8723-f2aa08b66eab', 'upw_maturity_assessment', 'Placement preferences based on gender identity', null, null, '2021-09-27 14:50:00', null),
       ('95000412-07cb-49aa-8821-6712880e3097', 'upw_risk_section', 'Risk section', null, null, '2021-09-27 14:50:00', null),
       ('1255f7c4-81fe-494c-b269-38f7261cb68c', 'upw_risk_of_harm_community', 'Risk of harm in the community', null, null, '2021-09-27 14:50:00', null),
       ('e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 'upw_managing_risk', 'Managing risk', null, null, '2021-09-27 14:50:00', null)
;

INSERT INTO assessment_schema_groups(assessment_schema_uuid, group_uuid)
VALUES ('9c318330-091a-4a97-842f-1146df9e9703', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4');

INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES
('5cefd241-cc51-4128-a343-cb7c438a9048', 'upw_cultural_religious_adjustment', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Cultural or religious adjustments?', null, null, null),
('e7f8205b-1f2a-4578-943c-154d2a6ee11e', 'upw_cultural_religious_adjustment_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('0238fbc4-e488-4e1e-858d-856532d1cf56', 'upw_cultural_religious_adjustment_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('4b62bf80-9801-49cd-b3da-fe8961571302', 'upw_placement_preference', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any placement preferences?', null, null, null),
('56da5e97-a871-4ac8-ad1e-5ca6001633d2', 'upw_placement_preferences', '2020-11-30 14:50:00', null, 'checkbox', '2a82f463-97d1-4de4-8601-23fca9215c17', null, null, null, 'PLACEMENT_PREFERENCES'),
('d1d849fe-1240-49db-bf60-692967a9e55e', 'upw_placement_preference_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('b2af0358-56fb-4e45-be76-b661ce829138', 'upw_placement_preference_by_gender_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Discuss placement options with the individual, based on their gender identity', 'Record their preference and the details of the conversation.', null, null),
('980f7936-682c-4174-91b6-3dcfc684c494', 'upw_placement_preference_by_gender_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('3fb1a17b-6657-4239-b020-512fa8f57a52', 'upw_maturity_assessment_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Maturity assessment', 'What action needs to be taken to support engagement or compliance with the community payback requirement?', null, null),
('7f4435a1-3d39-4758-aa56-67989e54d7d2', 'upw_maturity_assessment_details_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('30851ffd-90a3-430d-8239-0386076de177', 'upw_violent_offences', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Violent offences?', null, null, null),
('9feb81fc-3437-4762-88e1-b147db676c66', 'upw_violent_offences_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('6d18c3e9-a921-4b3b-aad3-a1c1f68d51c9', 'upw_frequent_dishonesty', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Frequent Dishonesty?', null, null, null),
('34db66d2-e95b-4ff5-b82d-d1c99fd8b80b', 'upw_frequent_dishonesty_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('2ef3e974-245e-485a-9dfb-2107fdde53a7', 'upw_sgo_identifier', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Has the individual been involved in serious group offending (SGO)?', null, null, null),
('3f964fd8-4677-4658-bddd-00ebeedcb932', 'upw_sgo_identifier_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('832e7462-f9db-4b01-832c-71ad21e5d70a', 'upw_control_issues', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Control issues or disruptive behaviour?', null, null, null),
('77451565-e9a3-47f2-880d-53f9db3e7225', 'upw_control_issues_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('b446b9db-d57a-4a29-9674-37099bdf1a00', 'upw_hate_based_behaviour', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Hate-based behaviour?', 'For example, homophobic or racially motivated', null, null),
('6ee5fa07-767d-46a5-8b56-dd05b7f21f45', 'upw_hate_based_behaviour_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('cc19e283-3ece-4f4b-af2f-c9f2b71e4947', 'upw_high_profile_person', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Is the individual vulnerable because they are a high-profile person?', 'For example, they are prominent on social media or are well-known in a particular area.', null, null),
('11f2b571-209d-49b0-b650-0ab7b6b0cde7', 'upw_high_profile_person_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('1f72a9d5-da65-4d9e-a49e-5e1150198ee6', 'upw_additional_rosh_info', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Additional risk of harm assessment information?', '', null, null),
('1fcbd1c4-1458-4bb2-a3bd-c02cc0e996dd', 'upw_additional_rosh_info_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('e457d288-aa0e-4000-8906-140bbd9d5b17', 'upw_rosh_community_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('42a18789-c039-4fe4-87f0-9943fde800ad', 'upw_location_exclusion_criteria', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Location restricted by victim exclusion criteria?', '', null, null),
('e3807492-5efa-4576-af84-d6d0957a6b95', 'upw_location_exclusion_criteria_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('1a6e8d30-99da-4c65-bcc6-986f0b39780a', 'upw_restricted_placement', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Close supervision or restricted placement recommended?', '', null, null),
('4f1c3ea4-6d1a-4d65-8068-e2916d1c6b7b', 'upw_restricted_placement_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('2ab8cd7c-2286-4269-8274-d85af97b6eab', 'upw_no_female_supervisor', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Recommend not to place with female supervisor?', '', null, null),
('f65d8471-cb53-4ec0-9b47-b63e09b6923a', 'upw_no_female_supervisor_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('2aba061a-fcab-495e-9fec-675e1348a11d', 'upw_no_male_supervisor', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Recommend not to place with male supervisor?', '', null, null),
('802678ea-c1e2-4fbb-abe1-ef6e6a06fedd', 'upw_no_male_supervisor_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('abb832b7-bb05-46c9-8c60-4dabc0a8cf98', 'upw_restrictive_orders', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Restrictive orders? (non-molestation, injunction etc.)', '', null, null),
('2aa628ae-a84f-4b15-8051-574bbbd4bb81', 'upw_restrictive_orders_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('642ac889-4a8a-4530-a98b-ca495d8c25f3', 'upw_risk_management_issues_individual', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any risk management issues for an individual placement?', '', null, null),
('75a2d1f0-cb1a-4614-a7bd-28b0f9fcd14b', 'upw_risk_management_issues_individual_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('20284f6e-7971-431f-a2f9-f5251afa1bf0', 'upw_risk_management_issues_supervised_group', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any risk management issues if working in a supervised group?', '', null, null),
('5c6922af-df8d-4bb6-af80-c9e68a913048', 'upw_risk_management_issues_supervised_group_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('3300e680-11c3-4069-8b38-081576553931', 'upw_alcohol_drug_issues', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Alcohol or drug issues with health and safety impact?', '', null, null),
('7318f4d3-068f-4e62-bbf8-cc430c096dd7', 'upw_alcohol_drug_issues_details', '2021-09-27 14:50:00', null, 'freetext', null, 'Give details', null, null, null),
('e8cc7779-498e-494b-8280-8cb088b3c6e9', 'upw_managing_risk_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null);

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES
('711caebc-2f8a-410b-9cd1-daf348410896', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 1, true, null, false),
('4204cf4b-814f-4cad-a311-1e7854f6edbe', '95000412-07cb-49aa-8821-6712880e3097', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 2, true, null, false),
-- Diversity top sections
('667e9967-275f-4d23-bd02-7b5e3f3e1647', '667e9967-275f-4d23-bd02-7b5e3f3e1647', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 1, true, null, false),
('d633d6e1-e252-4c09-a21c-c8cc558bce12', 'd633d6e1-e252-4c09-a21c-c8cc558bce12', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 2, true, null, false),
('b9114d94-2500-456e-8d2e-777703dfd6bc', 'b9114d94-2500-456e-8d2e-777703dfd6bc', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 3, true, null, false),
('b7b11bf3-836b-4cba-8723-f2aa08b66eab', 'b7b11bf3-836b-4cba-8723-f2aa08b66eab', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 4, true, null, false),
-- Diversity sections
('e8ea62cc-5a36-455c-8b36-0ececd678a1a', '5cefd241-cc51-4128-a343-cb7c438a9048', 'question', '667e9967-275f-4d23-bd02-7b5e3f3e1647', 1, true, null, false),
('48c305cb-c7d6-439e-adfc-661f834af036', 'e7f8205b-1f2a-4578-943c-154d2a6ee11e', 'question', '667e9967-275f-4d23-bd02-7b5e3f3e1647', 2, true, null, false),
('589dd2af-9bcf-48d5-9817-a6fe8b62c66e', '0238fbc4-e488-4e1e-858d-856532d1cf56', 'question', '667e9967-275f-4d23-bd02-7b5e3f3e1647', 3, true, null, false),

('190fa474-0119-45e4-8105-0aaa44e11d63', '4b62bf80-9801-49cd-b3da-fe8961571302', 'question', 'd633d6e1-e252-4c09-a21c-c8cc558bce12', 1, true, null, false),
('042d5308-c05b-49dd-896f-1a95e33d0ef4', '56da5e97-a871-4ac8-ad1e-5ca6001633d2', 'question', 'd633d6e1-e252-4c09-a21c-c8cc558bce12', 2, true, null, false),
('c8191d97-33c8-4e70-b564-24c240269dee', 'd1d849fe-1240-49db-bf60-692967a9e55e', 'question', 'd633d6e1-e252-4c09-a21c-c8cc558bce12', 3, true, null, false),

('252d7687-e94d-40e5-a3b9-139d95d846d4', 'b2af0358-56fb-4e45-be76-b661ce829138', 'question', 'b9114d94-2500-456e-8d2e-777703dfd6bc', 1, true, null, false),
('c27531b1-0729-4cc9-9557-4475b6f3b3f2', '980f7936-682c-4174-91b6-3dcfc684c494', 'question', 'b9114d94-2500-456e-8d2e-777703dfd6bc', 2, true, null, false),

('0ac554bb-cc99-473c-b7c4-b026cc5b7e35', '3fb1a17b-6657-4239-b020-512fa8f57a52', 'question', 'b7b11bf3-836b-4cba-8723-f2aa08b66eab', 1, true, null, false),
('fa4ea30a-3549-451a-b6c5-d157f642ab3b', '7f4435a1-3d39-4758-aa56-67989e54d7d2', 'question', 'b7b11bf3-836b-4cba-8723-f2aa08b66eab', 2, true, null, false),

-- Risk top sections
('85ede91e-95bb-4b47-b732-4b05d794a041', '1255f7c4-81fe-494c-b269-38f7261cb68c', 'group', '95000412-07cb-49aa-8821-6712880e3097', 1, true, null, false),
('4c02f821-a3b2-4b29-8238-4b4ed26e2108', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 'group', '95000412-07cb-49aa-8821-6712880e3097', 2, true, null, false),
-- Risk sections
('2c750777-45d0-4b96-8715-c8c20f4aa563', '30851ffd-90a3-430d-8239-0386076de177', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 1, true, null, false),
('5dd4651d-10ed-4dbe-8765-093ae2d85d09', '9feb81fc-3437-4762-88e1-b147db676c66', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 2, true, null, false),
('cda82f8a-7405-4059-8df0-e062d60ace97', '6d18c3e9-a921-4b3b-aad3-a1c1f68d51c9', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 3, true, null, false),
('f739dd2f-a6e4-4f97-97b7-b0c5479fe5c9', '34db66d2-e95b-4ff5-b82d-d1c99fd8b80b', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 4, true, null, false),
('d97a303f-2433-4920-9f98-990188be0feb', '2ef3e974-245e-485a-9dfb-2107fdde53a7', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 5, true, null, false),
('03efb99e-f659-454d-bf26-b61f8a57972d', '3f964fd8-4677-4658-bddd-00ebeedcb932', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 6, true, null, false),
('73808c0a-e1c3-4a9c-8b15-a889531c6fa9', '832e7462-f9db-4b01-832c-71ad21e5d70a', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 7, true, null, false),
('512fe8c1-bd0b-40a0-a9de-9ac494649c31', '77451565-e9a3-47f2-880d-53f9db3e7225', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 8, true, null, false),
('dc256fe6-1bbb-4153-8b57-42d5793a14e3', 'b446b9db-d57a-4a29-9674-37099bdf1a00', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 9, true, null, false),
('b8517ee2-f566-464e-a533-f35d92f8a65b', '6ee5fa07-767d-46a5-8b56-dd05b7f21f45', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 10, true, null, false),
('044eceff-e3f1-4255-8c3b-748d153ba3ca', 'cc19e283-3ece-4f4b-af2f-c9f2b71e4947', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 11, true, null, false),
('4874fe38-ce05-4c2c-ae6d-9af1f2b53495', '11f2b571-209d-49b0-b650-0ab7b6b0cde7', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 12, true, null, false),
('a043a684-3019-4732-a073-c36114f880bf', '1f72a9d5-da65-4d9e-a49e-5e1150198ee6', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 13, true, null, false),
('4c235f69-72cb-443a-ba2c-ba5da911c56b', '1fcbd1c4-1458-4bb2-a3bd-c02cc0e996dd', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 14, true, null, false),
('0931dfa9-41a9-4e01-b2e5-2ac3feb83b8d', 'e457d288-aa0e-4000-8906-140bbd9d5b17', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 15, true, null, false),

('d0e5e64a-fa88-49b6-b8d5-9904744a11d2', '42a18789-c039-4fe4-87f0-9943fde800ad', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 1, true, null, false),
('6c6ce44b-edbd-43ab-8741-fd00eec0cc54', 'e3807492-5efa-4576-af84-d6d0957a6b95', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 2, true, null, false),
('e987ef0c-01f4-4de4-93ca-ffccb10e8b24', '1a6e8d30-99da-4c65-bcc6-986f0b39780a', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 3, true, null, false),
('1ea50f54-d107-4eaa-8d0e-582bf67718c3', '4f1c3ea4-6d1a-4d65-8068-e2916d1c6b7b', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 4, true, null, false),
('0c08b5bc-ac61-446d-a121-0d37480a48fd', '2ab8cd7c-2286-4269-8274-d85af97b6eab', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 5, true, null, false),
('faa32880-d147-4f05-a8de-cbe78753c0e4', 'f65d8471-cb53-4ec0-9b47-b63e09b6923a', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 6, true, null, false),
('12f6678f-6550-4816-9408-caf219e2e1a9', '2aba061a-fcab-495e-9fec-675e1348a11d', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 7, true, null, false),
('8d694146-f44e-4d25-974f-880564049b1e', '802678ea-c1e2-4fbb-abe1-ef6e6a06fedd', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 8, true, null, false),
('abb832b7-bb05-46c9-8c60-4dabc0a8cf98', 'abb832b7-bb05-46c9-8c60-4dabc0a8cf98', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 9, true, null, false),
('2aa628ae-a84f-4b15-8051-574bbbd4bb81', '2aa628ae-a84f-4b15-8051-574bbbd4bb81', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 10, true, null, false),
('422b4dbe-1d44-4bfc-888f-07ba58070cc8', '642ac889-4a8a-4530-a98b-ca495d8c25f3', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 11, true, null, false),
('05b19185-ed31-412a-aff2-00b8b1b85d02', '75a2d1f0-cb1a-4614-a7bd-28b0f9fcd14b', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 12, true, null, false),
('e6c3cd5c-8db2-4f6c-acf0-5c34aa0945b8', '20284f6e-7971-431f-a2f9-f5251afa1bf0', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 13, true, null, false),
('624f4d19-270d-42f6-9b3b-7ddc01d5aa37', '5c6922af-df8d-4bb6-af80-c9e68a913048', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 14, true, null, false),
('611041c7-8aec-4481-b515-f16f6d70f1e7', '3300e680-11c3-4069-8b38-081576553931', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 15, true, null, false),
('a21b9803-deec-473e-97ca-b1f79234c490', '7318f4d3-068f-4e62-bbf8-cc430c096dd7', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 16, true, null, false),
('ba9c39e7-66b5-403b-9d3f-e5375178190d', 'e8cc7779-498e-494b-8280-8cb088b3c6e9', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 17, true, null, false)
;

