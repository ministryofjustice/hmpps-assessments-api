INSERT INTO assessment_schema (assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
('9c318330-091a-4a97-842f-1146df9e9703', 'UPW', 'SOMETHING_IN_OASYS', 'END', 'Unpaid Work Assessment');

INSERT INTO answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES
('8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'yes-noillcomebacklater', '2021-09-27 14:50:00', null),
('2a82f463-97d1-4de4-8601-23fca9215c17', 'individual-mixed-femaleonly', '2021-09-27 14:50:00', null),
('7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', 'individualavailability', '2021-09-27 14:50:00', null),
('9fc83177-fd89-4755-89a5-69d0892cd25d', 'male-female', '2021-09-27 14:50:00', null),
('37943ca1-b977-410a-9e5a-75c2ab032abb', 'xs-s-m-l-xl-xxl-xxxl', '2021-09-27 14:50:00', null),
('ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '3-4-5-6-7-8-9-10-11-12-13-14', '2021-09-27 14:50:00', null)
;

INSERT INTO answer_schema (answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES
('51c9db72-9c69-4377-818c-b107572eab33', 'yes', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', '2021-09-27 14:50:00', null, 'YES', 'Yes'),
('70ca6d84-730d-45b2-b531-1d8d13fefadc', 'no_ill_come_back_later', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', '2021-09-27 14:50:00', null, 'NO_ILL_COME_BACK_LATER', 'No, I’ll come back later'),
('e8689a6c-2b53-4d6c-b9bf-3bbb0b4a6f85', 'individual', '2a82f463-97d1-4de4-8601-23fca9215c17', '2021-09-27 14:50:00', null, 'INDIVIDUAL', 'Individual'),
('dfaaa874-928b-4bd8-987a-5cdac16e8dba', 'mixed_group', '2a82f463-97d1-4de4-8601-23fca9215c17', '2021-09-27 14:50:00', null, 'MIXED_GROUP', 'Mixed group'),
('66646d01-19a5-4b9f-bfaf-a2f162897336', 'female_only_group', '2a82f463-97d1-4de4-8601-23fca9215c17', '2021-09-27 14:50:00', null, 'FEMALE_ONLY_GROUP', 'Female only group'),
('034a8964-7a54-4773-8224-87bb6c6a5ed8', 'monday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'MONDAY_MORNING', ''),
('78384548-62df-403f-99bb-4dd267ee0cc2', 'monday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'MONDAY_AFTERNOON', ''),
('74103423-a881-4572-810b-6993d58ff443', 'monday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'MONDAY_EVENING', ''),
('53acdc58-94ab-4049-8ebf-c4b2ba08f4f5', 'tuesday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'TUESDAY_MORNING', ''),
('bb7226bc-a231-43a7-a272-2d4d9a9d3d0b', 'tuesday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'TUESDAY_AFTERNOON', ''),
('5ed93e3e-67d6-402f-8de9-3b86d94d4f24', 'tuesday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'TUESDAY_EVENING', ''),
('4ab83101-e739-4618-b010-6983652585ae', 'wednesday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'WEDNESDAY_MORNING', ''),
('2d2fd1e4-2998-45ad-a7cf-e88f8570b4c2', 'wednesday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'WEDNESDAY_AFTERNOON', ''),
('f2147823-0211-4f05-bff7-84b445145d51', 'wednesday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'WEDNESDAY_EVENING', ''),
('4479a057-3159-49c5-9599-180f36428a78', 'thursday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'THURSDAY_MORNING', ''),
('1893becf-bf31-4697-b15e-d992dbf1812a', 'thursday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'THURSDAY_AFTERNOON', ''),
('a87c0229-1a9e-48a1-a319-d7a5101b0f43', 'thursday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'THURSDAY_EVENING', ''),
('29fdfd16-700c-4196-9c6e-58bf695e55f0', 'friday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'FRIDAY_MORNING', ''),
('d7ac7df6-d922-41a5-8b5d-379dca5250c7', 'friday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'FRIDAY_AFTERNOON', ''),
('d202e29b-ec9e-4192-98a4-345b07c653f1', 'friday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'FRIDAY_EVENING', ''),
('c3b508b5-0241-4c67-8d87-9fe364290c2d', 'saturday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'SATURDAY_MORNING', ''),
('59bb90b0-6216-4920-bc3b-b47a62bda837', 'saturday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'SATURDAY_AFTERNOON', ''),
('c072a9ff-fbda-42ae-a5e6-b79616b3bc55', 'saturday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'SATURDAY_EVENING', ''),
('6398406e-f323-474a-a5aa-cb65a1da75c7', 'sunday_morning', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'SUNDAY_MORNING', ''),
('0acb7955-6e01-4bd6-b47f-438b87db4cc0', 'sunday_afternoon', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'SUNDAY_AFTERNOON', ''),
('976e41ce-ef5b-431f-8cc7-cd5ea2d8c619', 'sunday_evening', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', '2021-09-27 14:50:00', null, 'SUNDAY_EVENING', ''),
('b34622fb-6c97-488e-8e13-96f2f43bbecf', 'male', '9fc83177-fd89-4755-89a5-69d0892cd25d', '2021-09-27 14:50:00', null, 'MALE', 'Male'),
('623581c2-9b99-4421-8a96-df51f0136e06', 'female', '9fc83177-fd89-4755-89a5-69d0892cd25d', '2021-09-27 14:50:00', null, 'FEMALE', 'Female'),

('bb427a45-d7d4-44a3-8b68-de2428fd92ea', 'xs',  '37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'XS', 'X-Small'),
('afe91b9f-0d1d-43ab-9dbe-7b81a73d67fa', 's',   '37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'S', 'Small'),
('d0d33422-17fd-4c9c-a90b-df4bab850363', 'm',   '37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'M', 'Medium'),
('121bd2a4-b57a-4ac5-b2bf-a676767dc264', 'l',   '37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'L', 'Large'),
('c51f6a7f-e8be-4147-a479-24f9019d30c2', 'xl',  '37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'XL', 'X-Large'),
('b1942513-23fa-4800-96bf-4933edef3cfa', 'xxl', '37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'XXL', 'XX-Large'),
('924c187c-5904-436a-9670-205d0b7dc646', 'xxxl','37943ca1-b977-410a-9e5a-75c2ab032abb', '2021-09-27 14:50:00', null, 'XXXL', 'XXX-Large'),

('aead43e0-ee10-4271-ba01-83a35428e996', '3', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '3', 'Size 3'),
('442d529a-750e-46e1-a7ec-869645c147c8', '4', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '4', 'Size 4'),
('794f872a-5348-450f-a84c-b5a3d9ecdd9f', '5', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '5', 'Size 5'),
('5e715a2e-b394-46b6-9cd1-bcf8009e66a4', '6', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '6', 'Size 6'),
('fbf22a78-bb07-44ca-813c-78ff4f09cdd3', '7', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '7', 'Size 7'),
('1e1c93be-d8a9-4257-8f9c-096af3ea7887', '8', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '8', 'Size 8'),
('5f6c70df-8f7e-4f69-813a-3b197a5366f4', '9', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '9', 'Size 9'),
('e97658a5-5526-4d54-84ad-d4f562076cb7', '10', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '10', 'Size 10'),
('caec7137-e7a7-4e48-aa42-fc2ec84df89d', '11', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '11', 'Size 11'),
('8ee47349-1a3d-4b03-a802-87f43765a6af', '12', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '12', 'Size 12'),
('caf6773d-50b2-4fa5-a802-333b37298b95', '13', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '13', 'Size 13'),
('90e91ab7-9919-409b-81a9-21ef4c62e2d1', '14', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', '2021-09-27 14:50:00', null, '14', 'Size 14')
;

INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 'upw_assessment', 'Unpaid Work Assessment', null, null, '2021-09-27 14:50:00', null),
       ('2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 'upw_diversity_section', 'Diversity section', null, null, '2021-09-27 14:50:00', null),
       ('667e9967-275f-4d23-bd02-7b5e3f3e1647', 'upw_cultural_info', 'Cultural or Religious adjustments', null, null, '2021-09-27 14:50:00', null),
       ('d633d6e1-e252-4c09-a21c-c8cc558bce12', 'upw_placement_preferences', 'Placement preferences', null, null, '2021-09-27 14:50:00', null),
       ('b9114d94-2500-456e-8d2e-777703dfd6bc', 'upw_placement_gender_preferences', 'Placement preferences based on gender identity', null, null, '2021-09-27 14:50:00', null),
       ('b7b11bf3-836b-4cba-8723-f2aa08b66eab', 'upw_maturity_assessment', 'Placement preferences based on gender identity', null, null, '2021-09-27 14:50:00', null),
       ('95000412-07cb-49aa-8821-6712880e3097', 'upw_risk_section', 'Risk section', null, null, '2021-09-27 14:50:00', null),
       ('1255f7c4-81fe-494c-b269-38f7261cb68c', 'upw_risk_of_harm_community', 'Risk of harm in the community', null, null, '2021-09-27 14:50:00', null),
       ('e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 'upw_managing_risk', 'Managing risk', null, null, '2021-09-27 14:50:00', null),
       ('76a2b2a9-69c9-42c1-8d79-46294790b212', 'upw_placement_restrictions_section', 'Placement restrictions section', null, null, '2021-09-27 14:50:00', null),
       ('6f8bcf8b-bf9c-4410-9111-07d94d32864a', 'upw_disabilities_and_mental_health', 'Disabilities and mental health', null, null, '2021-09-27 14:50:00', null),
       ('dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 'upw_other_health_issues', 'Other health issues', null, null, '2021-09-27 14:50:00', null),
       ('5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 'upw_travel_information', 'Travel information', null, null, '2021-09-27 14:50:00', null),
       ('28d07199-ceed-473f-8584-156b018d967a', 'upw_caring_commitments', 'Caring commitments', null, null, '2021-09-27 14:50:00', null),
       ('9cbfc1ad-a054-44ab-9827-fd1b429ef31d', 'upw_employment_education_skills_section', 'Employment, education and skills section', null, null, '2021-09-27 14:50:00', null),
       ('5f29f9f8-5926-4056-aa91-53369a5df9c6', 'upw_employment_education_skills', 'Employment, education and skills', null, null, '2021-09-27 14:50:00', null),
       ('31520ae0-dea2-482a-9b3c-ebade83863e6', 'upw_employment_training', 'Training & employment opportunities', null, null, '2021-09-27 14:50:00', null),
       ('b0238dcb-e12a-4d07-9986-7214139942d1', 'upw_placement_details_section', 'Placement details section', null, null, '2021-09-27 14:50:00', null),
       ('1519998a-ec33-45bb-8bb8-1e16ab9256ed', 'upw_intensive_working', 'Intensive working', null, null, '2021-09-27 14:50:00', null),
       ('dfd0c068-c53b-4769-bdb4-7cac7078515a', 'upw_availability_community_payback', 'Availability for Community Payback work', null, null, '2021-09-27 14:50:00', null),
       ('b0c6ed29-cde6-46a2-8c99-f26de7711a85', 'upw_working_equipment', 'Equipment', null, null, '2021-09-27 14:50:00', null),
       ('d674dfba-4e09-4673-9049-b5e1ee13285f', 'upw_declaration_section', 'Declaration section', null, null, '2021-09-27 14:50:00', null)
;

INSERT INTO assessment_schema_groups(assessment_schema_uuid, group_uuid)
VALUES ('9c318330-091a-4a97-842f-1146df9e9703', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4');

INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES
('5cefd241-cc51-4128-a343-cb7c438a9048', 'upw_cultural_religious_adjustment', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Cultural or religious adjustments?', null, null, null),
('e7f8205b-1f2a-4578-943c-154d2a6ee11e', 'upw_cultural_religious_adjustment_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('0238fbc4-e488-4e1e-858d-856532d1cf56', 'upw_cultural_religious_adjustment_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('4b62bf80-9801-49cd-b3da-fe8961571302', 'upw_placement_preference', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any placement preferences?', null, null, null),
('56da5e97-a871-4ac8-ad1e-5ca6001633d2', 'upw_placement_preferences', '2021-09-27 14:50:00', null, 'checkbox', '2a82f463-97d1-4de4-8601-23fca9215c17', null, null, null, 'PLACEMENT_PREFERENCES'),
('d1d849fe-1240-49db-bf60-692967a9e55e', 'upw_placement_preference_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('b2af0358-56fb-4e45-be76-b661ce829138', 'upw_placement_preference_by_gender_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Discuss placement options with the individual, based on their gender identity', 'Record their preference and the details of the conversation.', null, null),
('980f7936-682c-4174-91b6-3dcfc684c494', 'upw_placement_preference_by_gender_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('3fb1a17b-6657-4239-b020-512fa8f57a52', 'upw_maturity_assessment_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Maturity assessment', 'What action needs to be taken to support engagement or compliance with the community payback requirement?', null, null),
('7f4435a1-3d39-4758-aa56-67989e54d7d2', 'upw_maturity_assessment_details_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('30851ffd-90a3-430d-8239-0386076de177', 'upw_violent_offences', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Violent offences?', null, null, null),
('9feb81fc-3437-4762-88e1-b147db676c66', 'upw_violent_offences_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('6d18c3e9-a921-4b3b-aad3-a1c1f68d51c9', 'upw_frequent_dishonesty', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Frequent Dishonesty?', null, null, null),
('34db66d2-e95b-4ff5-b82d-d1c99fd8b80b', 'upw_frequent_dishonesty_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('2ef3e974-245e-485a-9dfb-2107fdde53a7', 'upw_sgo_identifier', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Has the individual been involved in serious group offending (SGO)?', null, null, null),
('3f964fd8-4677-4658-bddd-00ebeedcb932', 'upw_sgo_identifier_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('832e7462-f9db-4b01-832c-71ad21e5d70a', 'upw_control_issues', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Control issues or disruptive behaviour?', null, null, null),
('77451565-e9a3-47f2-880d-53f9db3e7225', 'upw_control_issues_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('b446b9db-d57a-4a29-9674-37099bdf1a00', 'upw_hate_based_behaviour', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Hate-based behaviour?', 'For example, homophobic or racially motivated', null, null),
('6ee5fa07-767d-46a5-8b56-dd05b7f21f45', 'upw_hate_based_behaviour_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('cc19e283-3ece-4f4b-af2f-c9f2b71e4947', 'upw_high_profile_person', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Is the individual vulnerable because they are a high-profile person?', 'For example, they are prominent on social media or are well-known in a particular area.', null, null),
('11f2b571-209d-49b0-b650-0ab7b6b0cde7', 'upw_high_profile_person_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('1f72a9d5-da65-4d9e-a49e-5e1150198ee6', 'upw_additional_rosh_info', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Additional risk of harm assessment information?', '', null, null),
('1fcbd1c4-1458-4bb2-a3bd-c02cc0e996dd', 'upw_additional_rosh_info_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('e457d288-aa0e-4000-8906-140bbd9d5b17', 'upw_rosh_community_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('42a18789-c039-4fe4-87f0-9943fde800ad', 'upw_location_exclusion_criteria', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Location restricted by victim exclusion criteria?', '', null, null),
('e3807492-5efa-4576-af84-d6d0957a6b95', 'upw_location_exclusion_criteria_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('1a6e8d30-99da-4c65-bcc6-986f0b39780a', 'upw_restricted_placement', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Close supervision or restricted placement recommended?', '', null, null),
('4f1c3ea4-6d1a-4d65-8068-e2916d1c6b7b', 'upw_restricted_placement_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('2ab8cd7c-2286-4269-8274-d85af97b6eab', 'upw_no_female_supervisor', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Recommend not to place with female supervisor?', '', null, null),
('f65d8471-cb53-4ec0-9b47-b63e09b6923a', 'upw_no_female_supervisor_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('2aba061a-fcab-495e-9fec-675e1348a11d', 'upw_no_male_supervisor', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Recommend not to place with male supervisor?', '', null, null),
('802678ea-c1e2-4fbb-abe1-ef6e6a06fedd', 'upw_no_male_supervisor_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('abb832b7-bb05-46c9-8c60-4dabc0a8cf98', 'upw_restrictive_orders', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Restrictive orders? (non-molestation, injunction etc.)', '', null, null),
('2aa628ae-a84f-4b15-8051-574bbbd4bb81', 'upw_restrictive_orders_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('642ac889-4a8a-4530-a98b-ca495d8c25f3', 'upw_risk_management_issues_individual', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any risk management issues for an individual placement?', '', null, null),
('75a2d1f0-cb1a-4614-a7bd-28b0f9fcd14b', 'upw_risk_management_issues_individual_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('20284f6e-7971-431f-a2f9-f5251afa1bf0', 'upw_risk_management_issues_supervised_group', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any risk management issues if working in a supervised group?', '', null, null),
('5c6922af-df8d-4bb6-af80-c9e68a913048', 'upw_risk_management_issues_supervised_group_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('3300e680-11c3-4069-8b38-081576553931', 'upw_alcohol_drug_issues', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Alcohol or drug issues with health and safety impact?', '', null, null),
('7318f4d3-068f-4e62-bbf8-cc430c096dd7', 'upw_alcohol_drug_issues_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('e8cc7779-498e-494b-8280-8cb088b3c6e9', 'upw_managing_risk_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),
('d35335af-4747-4842-8fbc-42ddca5bc78c', 'upw_disabilities', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Do any of the above affect the individual’s ability to engage with Community Payback?', '', null, null),
('c773474d-d5b1-4747-a866-b6e6bd42c96e', 'upw_disabilities_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Suggest  adjustments, if known [optional]', null, null, null),
('69e64e83-ce81-4796-8907-84b5dfac59f3', 'upw_disabilities_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('c11154bb-5675-4822-91ce-d870cdbcd46c', 'upw_allergies', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Allergies?', '', null, null),
('d20a594b-bff3-4520-a3cf-d95e49779a73', 'upw_allergies_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('1c34a46a-a094-4903-8c93-362b1dcb5718', 'upw_loss_consciousness', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Sudden loss of consciousness?', '', null, null),
('a148acf6-881f-4aed-8927-d22fef8e422b', 'upw_loss_consciousness_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('988aabc4-5a74-489c-be96-349197cada51', 'upw_epilepsy', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Epilepsy?', '', null, null),
('265ca722-9edd-4a6c-94c5-3d071582e952', 'upw_epilepsy_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('f79d4e3e-4855-4db2-9fec-7d628c2257ba', 'upw_other_health_issues', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Any other health issues?', '', null, null),
('7e8e8a40-426f-4489-8447-fbcd562d65fb', 'upw_other_health_issues_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('fb0d6e10-9a10-40e4-8ebc-ffcb2c46ffbb', 'upw_pregnancy', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Pregnant or recently given birth?', '', null, null),
('c9e7ff29-a9bf-47ea-81b2-c0a8f025ade8', 'upw_pregnancy_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('155e1596-9f83-4e0a-8fa7-e36f9ed2b2e5', 'upw_health_issues_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('20a3e6c7-f4a3-4598-ab49-c9cea084921a', 'upw_travel_information', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any travel issues that will affect their placement?', '', null, null),
('c47786e4-0c3d-4ea4-b81c-8547254ab12a', 'upw_travel_information_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('9bb13ffd-c4c5-494e-a519-90857bfbdb3f', 'upw_driving_licence', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have a valid driving licence?', '', null, null),
('381330bc-ac08-42b5-b092-d28950106854', 'upw_vehicle', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Do they have access to a vehicle?', '', null, null),
('6d4aac44-9681-4a2f-81af-57b1f78f6779', 'upw_public_transport', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Is public transport available and accessible to the individual?', '', null, null),
('1dabae67-1b71-4190-ba31-aae51d575f31', 'upw_travel_information_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('1661fcd6-996c-449f-aaa5-69451424be74', 'upw_caring_commitments', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there carer commitments?', '', null, null),
('bd383ed7-ba4a-42da-abc0-aacd8ff3e14f', 'upw_caring_commitments_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('7f7896f3-62b2-455a-9565-08538085ced5', 'upw_caring_commitments_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('548f23e4-7975-46bf-b801-26d1c81738f4', 'upw_reading_writing_difficulties', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any difficulties with reading or writing?', '', null, null),
('0e3e35d4-2dbc-4234-8fa0-2f7d7a0f5409', 'upw_reading_writing_difficulties_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('594ecb57-e8f5-4cda-b0d3-05abe8e01c30', 'upw_work_skills', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any work skills or experience that could be used while carrying out Community Payback?', '', null, null),
('59236813-d351-4fe9-a150-8b9e40e11248', 'upw_work_skills_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('620e586c-5573-40b1-bf35-9df68bc97bf8', 'upw_future_work_plans', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have future work plans that could be supported through a Community Payback placement?', 'For example: retail, gardening etc', null, null),
('6b738b3c-b935-4aff-b56f-e62d0c74fe0a', 'upw_future_work_plans_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('e2529cda-e57b-44c6-bf08-466caf8be43d', 'upw_employment_education_skills_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('d2a47e6c-06eb-4ebd-abe2-786edd916acd', 'upw_education_training_need', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have an education, training or employment-related need?', '', null, null),
('4fce15f3-6d62-4ade-aa26-b0fc886e28cb', 'upw_education_training_need_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('c0371601-e15b-48c4-a736-a59cbf804669', 'upw_individual_commitment', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual agree to use the maximum entitlement of their hours on this activity?', '', null, null),
('4dfb15ae-0d58-4672-98ec-494773e9a701', 'upw_individual_commitment_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('315db0b1-66ac-4a3e-93bb-f9b7545fddb4', 'upw_employment_training_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('e7d21b47-6653-48be-845e-8a28f529bf9e', 'upw_eligibility_intensive_working', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Is the individual eligible for intensive working?', '', null, null),
('303568a9-7f7c-42ac-a7e9-cc78531528af', 'upw_eligibility_intensive_working_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null, null),
('d097ddde-337a-4777-a34b-ff3ab15cb698', 'upw_recommended_hours_start_order', '2021-09-27 14:50:00', null, 'numeric', null, 'Recommended hours per week in addition to statutory minimum, at the start of the order', '7 hours is the standard working day. Maximum of 21 additional hours per week.', null, null),
('fbf62e33-03c3-4893-8ff6-92aa05827c40', 'upw_recommended_hours_midpoint_order', '2021-09-27 14:50:00', null, 'numeric', null, 'Recommended hours per week in addition to statutory minimum, at the midpoint of the order', '7 hours is the standard working day. Maximum of 21 additional hours per week.', null, null),
('6beae16e-7f58-4270-8e2c-f88aa5ca9fd4', 'upw_twenty_eight_hours_working_week_details', '2021-09-27 14:50:00', null, 'textarea', null, 'At what point should the individual be expected to reach a 28-hour working week?', '', null, null),
('fbee7d2a-3c6a-4aa7-bf7f-067674f07a8e', 'upw_eligibility_intensive_working_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', 'upw_individual_availability', '2021-09-27 14:50:00', null, 'checkbox', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', 'When is the individual available for  work?', null, null, 'INDIVIDUAL_AVAILABILITY'),
('54c8c012-847d-44c7-b9d0-3f45874c96b5', 'upw_individual_availability_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Additional availability information [Optional]', null, null, null),
('b75f9b88-6e52-4543-a280-18866938d1bc', 'upw_individual_availability_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('97258aa2-be8b-4a7a-a9e5-67c9c1965274', 'upw_male_female_clothing', '2021-09-27 14:50:00', null, 'radio', '9fc83177-fd89-4755-89a5-69d0892cd25d', 'Male or female clothing required?', null, null, 'CLOTHING'),
('fe4efc12-0a84-4875-a90f-317e96e320b5', 'upw_waterproof_clothing', '2021-09-27 14:50:00', null, 'radio', '37943ca1-b977-410a-9e5a-75c2ab032abb', 'Waterproof clothing', null, null, 'WATERPROOF_CLOTHING'),
('451f6e52-a314-4e7d-83ca-afd8dfc6dc43', 'upw_footwear_size', '2021-09-27 14:50:00', null, 'dropdown', 'ffe5ecbf-1669-46c9-b40f-4ac82dba0629', 'Footwear', 'Size (UK)', null, 'FOOTWEAR_SIZE'),
('5b1046e3-53f1-44d8-97db-5fd543f4382c', 'upw_equipment_complete', '2021-09-27 14:50:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark this section as complete?', '', null, null),

('7beed4f4-0f3b-4b16-85f0-90377a350f1e', 'upw_declaration_confirmation', '2021-09-27 14:50:00', null, 'checkbox', null, 'I confirm the individual has received details of their Community Payback Induction Session', null, null, '')
;

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES
('711caebc-2f8a-410b-9cd1-daf348410896', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 1, true, null, false),
('4204cf4b-814f-4cad-a311-1e7854f6edbe', '95000412-07cb-49aa-8821-6712880e3097', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 2, true, null, false),
('92785dc3-2c38-4066-8b3a-ef9fd0094a57', '76a2b2a9-69c9-42c1-8d79-46294790b212', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 3, true, null, false),
('6de51fe3-89b6-4488-b931-27fd8530dacd', '9cbfc1ad-a054-44ab-9827-fd1b429ef31d', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 3, true, null, false),
('02410525-acd4-4b88-aa0c-1760c0fabe97', 'b0238dcb-e12a-4d07-9986-7214139942d1', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 4, true, null, false),
('60cff2ba-42b8-4ef0-a14d-7de5a27da664', 'd674dfba-4e09-4673-9049-b5e1ee13285f', 'group', 'ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 5, true, null, false),

-- Diversity top sections
('d46d6b16-6851-45fc-a4e9-28bfcadef3b0', '667e9967-275f-4d23-bd02-7b5e3f3e1647', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 1, true, null, false),
('17f91134-825f-4311-b57e-bf70fad072a1', 'd633d6e1-e252-4c09-a21c-c8cc558bce12', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 2, true, null, false),
('d070ae03-0c39-49e6-bb7a-c068bda7c1b4', 'b9114d94-2500-456e-8d2e-777703dfd6bc', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 3, true, null, false),
('52c9ef41-1931-42ae-9459-5d03877ec514', 'b7b11bf3-836b-4cba-8723-f2aa08b66eab', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 4, true, null, false),
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
('ba9c39e7-66b5-403b-9d3f-e5375178190d', 'e8cc7779-498e-494b-8280-8cb088b3c6e9', 'question', 'e2106fd9-8aea-4ecb-a437-a13c1e3c6703', 17, true, null, false),

-- Placement restrictions top sections
('1c1e7404-c39f-4548-9e00-6db4bdcd83d7', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 'group', '76a2b2a9-69c9-42c1-8d79-46294790b212', 1, true, null, false),
('f6833779-146e-414e-ab47-f0766083c816', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 'group', '76a2b2a9-69c9-42c1-8d79-46294790b212', 2, true, null, false),
('586ae887-5674-412b-8483-4c7c0856ec6c', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 'group', '76a2b2a9-69c9-42c1-8d79-46294790b212', 3, true, null, false),
('30169891-4863-436a-865a-cbaed7e6d4ad', '28d07199-ceed-473f-8584-156b018d967a', 'group', '76a2b2a9-69c9-42c1-8d79-46294790b212', 4, true, null, false),
-- Placement restrictions sections
('8538fb4b-4ed5-402a-a732-8489d8d8f86e', 'd35335af-4747-4842-8fbc-42ddca5bc78c', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 1, true, null, false),
('e2af28fc-d974-462a-8bbd-21dcd8392860', 'c773474d-d5b1-4747-a866-b6e6bd42c96e', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 2, true, null, false),
('fecfa7af-b033-427b-9fdc-c4b3141d2d59', '69e64e83-ce81-4796-8907-84b5dfac59f3', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 3, true, null, false),

('5663f619-24d7-4dac-9ab8-e6a26edfdec0', 'c11154bb-5675-4822-91ce-d870cdbcd46c', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 1, true, null, false),
('a7a06357-2a8d-4c20-b388-90f890d88e67', 'd20a594b-bff3-4520-a3cf-d95e49779a73', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 2, true, null, false),
('ddea4f94-3862-4e90-888b-c435682a4cf2', '1c34a46a-a094-4903-8c93-362b1dcb5718', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 3, true, null, false),
('d928aecf-187f-4872-8a9e-7596b8ad8ad5', 'a148acf6-881f-4aed-8927-d22fef8e422b', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 4, true, null, false),
('2a9b4141-a1e5-4a25-9311-380ca47f5271', '988aabc4-5a74-489c-be96-349197cada51', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 5, true, null, false),
('369492cf-b5fb-4b47-9d72-c3ea9c82880f', '265ca722-9edd-4a6c-94c5-3d071582e952', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 6, true, null, false),
('da0b3ddd-7f24-4c44-87e7-6ab96fb75971', 'f79d4e3e-4855-4db2-9fec-7d628c2257ba', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 7, true, null, false),
('d26f777d-6122-4736-874c-b495044b3b4b', '7e8e8a40-426f-4489-8447-fbcd562d65fb', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 8, true, null, false),
('2d769cac-bd47-4155-b5e8-90e788730647', 'fb0d6e10-9a10-40e4-8ebc-ffcb2c46ffbb', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 9, true, null, false),
('9323811b-dec7-460c-9ec8-e5c93d0a9339', 'c9e7ff29-a9bf-47ea-81b2-c0a8f025ade8', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 10, true, null, false),
('df14557b-3624-4ed8-879c-21fdb32fa154', '155e1596-9f83-4e0a-8fa7-e36f9ed2b2e5', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 11, true, null, false),

('f5d7f236-202d-41ee-84d4-47ec40e00c51', '20a3e6c7-f4a3-4598-ab49-c9cea084921a', 'question', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 1, true, null, false),
('ac274481-dbaa-4319-9612-c1c1033e25bf', 'c47786e4-0c3d-4ea4-b81c-8547254ab12a', 'question', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 2, true, null, false),
('11381e01-1655-42de-bfb6-3cfad5278e2b', '9bb13ffd-c4c5-494e-a519-90857bfbdb3f', 'question', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 3, true, null, false),
('b8cc3d33-768b-435b-a5af-419ae1304c1a', '381330bc-ac08-42b5-b092-d28950106854', 'question', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 4, true, null, false),
('c0443d1d-79d6-41e5-a9bb-3bbbdd8d3d5d', '6d4aac44-9681-4a2f-81af-57b1f78f6779', 'question', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 5, true, null, false),
('573c77c9-ad2a-4582-a958-66afb57636bd', '1dabae67-1b71-4190-ba31-aae51d575f31', 'question', '5460f64f-4bcb-40bc-a91d-0e90a4ea6a9b', 6, true, null, false),

('c1d55159-1ceb-4204-9575-c8d41e91ff7f', '1661fcd6-996c-449f-aaa5-69451424be74', 'question', '28d07199-ceed-473f-8584-156b018d967a', 1, true, null, false),
('9bdc6f50-2601-4a31-8a05-2341dbf4af6e', 'bd383ed7-ba4a-42da-abc0-aacd8ff3e14f', 'question', '28d07199-ceed-473f-8584-156b018d967a', 2, true, null, false),
('27eb851d-4272-4498-890c-d84e93ac1c35', '7f7896f3-62b2-455a-9565-08538085ced5', 'question', '28d07199-ceed-473f-8584-156b018d967a', 3, true, null, false),

-- Employment, education and skills information top sections
('6ace857b-c4c7-44af-b958-583facb7bdee', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 'group', '9cbfc1ad-a054-44ab-9827-fd1b429ef31d', 1, true, null, false),
('737d98d3-0652-489c-a34a-d7b9905ab7a8', '31520ae0-dea2-482a-9b3c-ebade83863e6', 'group', '9cbfc1ad-a054-44ab-9827-fd1b429ef31d', 2, true, null, false),
-- Employment, education and skills information sections
('69067e81-fea8-4e83-971f-378c633233c5', '548f23e4-7975-46bf-b801-26d1c81738f4', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 1, true, null, false),
('62052a2a-a0cc-4ab5-aca9-b61cb06bd91c', '0e3e35d4-2dbc-4234-8fa0-2f7d7a0f5409', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 2, true, null, false),
('207e9b28-6655-44f7-8065-f8cb287a3d53', '594ecb57-e8f5-4cda-b0d3-05abe8e01c30', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 3, true, null, false),
('00459dc9-3bb1-49cc-9224-fa299e2c223f', '59236813-d351-4fe9-a150-8b9e40e11248', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 4, true, null, false),
('d92772a2-110c-4cbf-90d3-52ed1b232d43', '620e586c-5573-40b1-bf35-9df68bc97bf8', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 5, true, null, false),
('6d8f3b4f-5792-451a-be8a-f9e143029c76', '6b738b3c-b935-4aff-b56f-e62d0c74fe0a', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 6, true, null, false),
('19cb87ee-38b3-4daf-b352-8f2ab1942c00', 'e2529cda-e57b-44c6-bf08-466caf8be43d', 'question', '5f29f9f8-5926-4056-aa91-53369a5df9c6', 7, true, null, false),

('ec5e8ce6-97a6-4989-bf30-2183ead9377a', 'd2a47e6c-06eb-4ebd-abe2-786edd916acd', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 1, true, null, false),
('c9180c1d-a57a-4d2c-a5c1-7cb57ba0b02e', '4fce15f3-6d62-4ade-aa26-b0fc886e28cb', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 2, true, null, false),
('12c511f4-1a7b-4819-b458-283ce104ac69', 'c0371601-e15b-48c4-a736-a59cbf804669', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 3, true, null, false),
('92759422-b14d-4054-9a63-e97d747604eb', '4dfb15ae-0d58-4672-98ec-494773e9a701', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 4, true, null, false),
('8773d9f1-b8df-49f4-b6ea-6d4b41dcf723', '315db0b1-66ac-4a3e-93bb-f9b7545fddb4', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 5, true, null, false),

-- Placement details top sections
('379fb07a-c20d-42a4-bf08-12833ac04b2e', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 'group', 'b0238dcb-e12a-4d07-9986-7214139942d1', 1, true, null, false),
('6116ed3c-abc0-4f63-bc4f-be7264200c17', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 'group', 'b0238dcb-e12a-4d07-9986-7214139942d1', 2, true, null, false),
('a5cc654b-3680-4aac-a948-812ca7b75263', 'b0c6ed29-cde6-46a2-8c99-f26de7711a85', 'group', 'b0238dcb-e12a-4d07-9986-7214139942d1', 3, true, null, false),
-- Placement details questions
('5176e9d8-87c1-4e44-ac12-dbe8f51c65d9', 'e7d21b47-6653-48be-845e-8a28f529bf9e', 'question', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 1, true, null, false),
('04015a85-a4e9-45f8-b66b-1cbff39e7e0e', '303568a9-7f7c-42ac-a7e9-cc78531528af', 'question', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 2, true, null, false),
('e6be94bf-13f1-46ca-825b-a1e2de3acd4b', 'd097ddde-337a-4777-a34b-ff3ab15cb698', 'question', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 3, true, null, false),
('7979c64e-5d1c-4ea6-99b1-a0a10c760cef', 'fbf62e33-03c3-4893-8ff6-92aa05827c40', 'question', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 4, true, null, false),
('7dfd0ef9-5b9b-4f2f-952f-66488ff40fd6', '6beae16e-7f58-4270-8e2c-f88aa5ca9fd4', 'question', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 5, true, null, false),
('d5d2a5b2-fdfe-4eae-8881-3a4d6693f607', 'fbee7d2a-3c6a-4aa7-bf7f-067674f07a8e', 'question', '1519998a-ec33-45bb-8bb8-1e16ab9256ed', 6, true, null, false),

('78347a79-9d9a-4236-bd3a-1beaefce0864', '7816fe5c-3beb-46d1-b2af-4a0a8f30aca0', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 1, true, null, false),
('3d70670b-5012-4043-9472-0ba356f66dd3', '54c8c012-847d-44c7-b9d0-3f45874c96b5', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 2, true, null, false),
('330969da-8d66-4a06-8c15-1e813851d7c2', 'b75f9b88-6e52-4543-a280-18866938d1bc', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 3, true, null, false),

('c7694b99-f276-40f1-bb11-260a8d671b04', '97258aa2-be8b-4a7a-a9e5-67c9c1965274', 'question', 'b0c6ed29-cde6-46a2-8c99-f26de7711a85', 1, true, null, false),
('0433da52-d48e-4030-acd2-faee54d5b622', 'fe4efc12-0a84-4875-a90f-317e96e320b5', 'question', 'b0c6ed29-cde6-46a2-8c99-f26de7711a85', 2, true, null, false),
('b624a6c4-88b6-4253-9df0-5599a0d201ff', '451f6e52-a314-4e7d-83ca-afd8dfc6dc43', 'question', 'b0c6ed29-cde6-46a2-8c99-f26de7711a85', 3, true, null, false),
('e073f4f8-288b-40d4-8236-3ec6a5fbc1fd', '5b1046e3-53f1-44d8-97db-5fd543f4382c', 'question', 'b0c6ed29-cde6-46a2-8c99-f26de7711a85', 4, true, null, false),

-- Declaration section questions
('f9f4330f-9420-491c-b566-7f3a2d3f5dbf', '7beed4f4-0f3b-4b16-85f0-90377a350f1e', 'question', 'd674dfba-4e09-4673-9049-b5e1ee13285f', 1, true, null, false);

INSERT INTO question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES
    ('e7f8205b-1f2a-4578-943c-154d2a6ee11e', '5cefd241-cc51-4128-a343-cb7c438a9048', 'YES', '2020-11-30 14:50:00', true),
    ('56da5e97-a871-4ac8-ad1e-5ca6001633d2', '4b62bf80-9801-49cd-b3da-fe8961571302', 'YES', '2020-11-30 14:50:00', true);