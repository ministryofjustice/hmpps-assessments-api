-- Add new groups
INSERT INTO hmppsassessmentsschemas.grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES
    ('89e92087-2668-4cc3-b307-d88fb43386aa', 'other_adjustments', 'Other adjustments', null, null, '2021-09-27 14:50:00', null);

-- Add new questions
INSERT INTO hmppsassessmentsschemas.question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
--  Risks:
('cea77882-0891-4930-9190-89f33c69e985', 'risks_history_of_offending_against_vulnerable_adults', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'History of offending against vulnerable adults?', 'For example, domestic abuse or offences involving breach of trust', null),
('77b31e79-4123-4d12-8f27-5f38d30c06ff', 'risks_history_of_offending_against_vulnerable_adults_yes_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null),
--  Diversity information:
('9754aa9f-26eb-4f06-b895-7a394b26adc1', 'diversity_information_trauma', '2021-09-27 14:50:00', null, 'textarea', null, 'Trauma', 'It is important to outline any history of trauma the individual might have experienced. This can be considered in placement they are allocated to and support the project supervisor in taking a trauma informed approach', null),
('a4447794-58ef-44bb-9505-acd6e1d4b847', 'diversity_information_gender', '2021-09-27 14:50:00', null, 'textarea', null, 'Gender', 'If the individual is female please outline if they would prefer, if possible, to be placed in a single gender group or individual placement.', null),
('17e86108-a0d7-4c78-b561-039893d1cd02', 'diversity_information_neurodiversity', '2021-09-27 14:50:00', null, 'textarea', null, 'Neurodiversity', 'Does the individual have an identified Neurodivergent condition eg Autism Spectrum Disorder, Attention Deficit Hyperactivity Disorder, Developmental Co-ordination Disorder, Dyslexia. Are any adjustments that need to be considered?', null),
('538a30a8-515c-4d40-9b3d-60979d8c11d6', 'diversity_information_mobility', '2021-09-27 14:50:00', null, 'textarea', null, 'Transport/Mobility', 'Is the individual able to report directly to site? Do they have access to their own transport? Is the person able to access public transport that would allow them to travel to the project prior to the start time?', null),
('a892ea05-5edc-4190-81f4-558da50ec995', 'diversity_information_maturity_assessment', '2021-09-27 14:50:00', null, 'textarea', null, 'Maturity Assessment','<p class="govuk-hint">If aged between 18-25 has the Maturity Assessment been completed?</p><p class="govuk-hint">Please Enter the Score:</p><p class="govuk-hint">Maturity Assessment should be completed on all individuals aged 18-25 prior to completing unpaid work:</p>', null),
('03976477-cf01-4db7-8b01-1a00e7fab6ce', 'diversity_information_maturity', '2021-09-27 14:50:00', null, 'textarea', null, 'Maturity','If assessed as low maturity what action needs to be taken to support engagement/compliance with the UPW Requirement?', null),
('8934d727-0f8f-454d-9cf5-2e2e5cad5d2e', 'other_adjustments_complete', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Mark additional information section as complete?', null, null),
--  Training and employment opportunities:
('669a3119-77f0-447c-8842-b02107128917', 'training_and_employment_factors_preventing', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Is there any reason that would prevent the individual from completing the 3 mandatory Community Campus Courses online? Or any other online learning?', '<p class=govuk-body>This could be as result of restrictive conditions such as a Sexual Harm Prevention Order or Serious Crim Prevention Order that prevents internet access.</p><p class=govuk-body>It also could be due to having insufficient internet access or lack of literacy issues.</p>', null),
('0e763894-73d0-4281-bf8b-ccaf467dee0d', 'training_and_employment_factors_preventing_yes_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null),
--  Availability for community payback:
('a40bbd0e-eaaa-4084-aa51-d145ef887806', 'availability_intensive_working', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'If Person on Probation is unemployed, are they available to work intensively? (Up to a maximum 28 Hours a week)', null, null),
('ef91ec6e-85f1-424a-8c4c-26e85b75093f', 'availability_intensive_working_no_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null),
('ac73023e-6638-443a-96ef-8ff912fcc6da', 'availability_availability_weekdays', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'If Person on Probation is unemployed, are they available to work Mon-Fri?', null, null),
('9f4a3cec-c219-4dde-9bab-6e7499ea8b4c', 'availability_availability_weekdays_no_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null),
('6ce7a59a-17d3-4ed6-9d8f-069483f9e720', 'availability_want_to_considered_for_intensive_working', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'If the person on probation is employed, do they want to be considered for intensive working?', null, null),
('317f8854-e026-4489-b8b8-ea944b6aa28a', 'availability_want_to_considered_for_intensive_working_yes_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null);

-- Add dependencies for new questions
INSERT INTO hmppsassessmentsschemas.question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES
    ('77b31e79-4123-4d12-8f27-5f38d30c06ff', 'cea77882-0891-4930-9190-89f33c69e985', 'YES', '2020-11-30 14:50:00', true),
    ('0e763894-73d0-4281-bf8b-ccaf467dee0d', '669a3119-77f0-447c-8842-b02107128917', 'YES', '2020-11-30 14:50:00', true),
    ('ef91ec6e-85f1-424a-8c4c-26e85b75093f', 'a40bbd0e-eaaa-4084-aa51-d145ef887806', 'NO', '2020-11-30 14:50:00', true),
    ('9f4a3cec-c219-4dde-9bab-6e7499ea8b4c', 'ac73023e-6638-443a-96ef-8ff912fcc6da', 'NO', '2020-11-30 14:50:00', true),
    ('317f8854-e026-4489-b8b8-ea944b6aa28a', '6ce7a59a-17d3-4ed6-9d8f-069483f9e720', 'YES', '2020-11-30 14:50:00', true);

-- Add new questions to groups
-- Note: I've just used some arbitrary values for display order here as it's not used AFAIK
INSERT INTO hmppsassessmentsschemas.question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
--  Risks:
('b5d80dd7-cee6-47b4-9a7d-027ac7e3faf3', 'cea77882-0891-4930-9190-89f33c69e985', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 98, false),
('82ea870d-4b07-4275-978f-497a5991f681', '77b31e79-4123-4d12-8f27-5f38d30c06ff', 'question', '1255f7c4-81fe-494c-b269-38f7261cb68c', 99, false),
--  Diversity information:
('b3dc53be-d6b0-4aa2-98af-5eb4e83fb332', '89e92087-2668-4cc3-b307-d88fb43386aa', 'group', '2bd35476-ac9b-4f15-ac7d-ea6943ccc120', 4, false),
('d6a108e3-088f-4c53-9f0e-ea0d85104752', '9754aa9f-26eb-4f06-b895-7a394b26adc1', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 1, false),
('97c57ca4-5eb4-4c26-a668-65b3f9a69b73', 'a4447794-58ef-44bb-9505-acd6e1d4b847', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 2, false),
('262f08e6-494b-4525-8653-6359225e9f86', '17e86108-a0d7-4c78-b561-039893d1cd02', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 3, false),
('aa5d2947-a345-4b0f-8753-7130ae3b952b', '538a30a8-515c-4d40-9b3d-60979d8c11d6', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 4, false),
('af4622db-567e-45cf-9031-3c51118c5a42', 'a892ea05-5edc-4190-81f4-558da50ec995', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 5, false),
('d9acff35-d112-4ca6-8dbc-cb48e05b037d', '03976477-cf01-4db7-8b01-1a00e7fab6ce', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 6, false),
('21636fea-1838-4c6a-8524-c30574d78a80', '8934d727-0f8f-454d-9cf5-2e2e5cad5d2e', 'question', '89e92087-2668-4cc3-b307-d88fb43386aa', 7, false),
--  Training and employment opportunities:
('560964f3-12d8-45c4-9a8e-1ccec4ffe406', '669a3119-77f0-447c-8842-b02107128917', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 98, false),
('f0b14e90-f059-4d44-92ce-7d26f143e81a', '0e763894-73d0-4281-bf8b-ccaf467dee0d', 'question', '31520ae0-dea2-482a-9b3c-ebade83863e6', 99, false),
--  Availability for community payback:
('4e306b97-147e-4bab-8c8c-809bcfb2dfac', 'a40bbd0e-eaaa-4084-aa51-d145ef887806', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 94, false),
('e50b8fd5-0e91-408b-a225-b8779736d3cb', 'ef91ec6e-85f1-424a-8c4c-26e85b75093f', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 95, false),
('6f09b2ca-cd5a-4d0a-9867-6b8d4c1391c7', 'ac73023e-6638-443a-96ef-8ff912fcc6da', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 96, false),
('7ea67250-fb21-4312-9f9c-9f0221448449', '9f4a3cec-c219-4dde-9bab-6e7499ea8b4c', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 97, false),
('15db5847-154e-48b9-89ed-feac6afdc277', '6ce7a59a-17d3-4ed6-9d8f-069483f9e720', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 98, false),
('78701bee-e34d-4d9d-8b41-eb011d6c1605', '317f8854-e026-4489-b8b8-ea944b6aa28a', 'question', 'dfd0c068-c53b-4769-bdb4-7cac7078515a', 99, false);

-- Update content for existing questions
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'When allocating a person who has a current or previous conviction for a sexual offence to a project, consideration must be given to avoiding any contact with children or vulnerable people. Where necessary, staff should be aware of the nature of the convictions and arrangements for the individual’s safety in the event of any disclosure about these offences.'
WHERE question_code = 'history_sexual_offending';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Does this individual have a conviction, or is there any evidence to indicate that they pose a risk of harm to children? Community Payback work should not include direct personal care or supervision of a child or vulnerable adult. If Community Payback work takes place on premises to which (a) the public do not have access and (b) children or vulnerable adults are present, then before a placement can be considered there must be a clear record on the case management system that the individual is not barred from working with children or vulnerable adults.'
WHERE question_code = 'poses_risk_to_children';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'In order to manage the dynamics of the group, supervisors will need to know if an individual has a history of violence or using weapons. Please check their previous convictions and any other known information.'
WHERE question_code = 'violent_offences';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Placement coordinators need to limit opportunities for further offending when placing an individual on a project. Please detail any recent history of theft or fraudulent offending.'
WHERE question_code = 'acquisitive_offending';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = '<p class="govuk-hint">Is there any intelligence or evidence of this individual:</p><ul class="govuk-list govuk-list--bullet govuk-hint"><li>identifying or associating with a known gang or group and their location</li><li>being in contact with people related to the gang or group that they identify with</li><li>being in contact with people who associate with rival gangs or groups</li><li>being present in areas considered to be frequented by other street gangs or groups</li></ul>'
WHERE question_code = 'sgo_identifier';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please record any evidence of the individual having a history of problems with authority or offending when in groups etc Please document any history of conduct issues whilst in custody.'
WHERE question_code = 'control_issues';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please record any evidence of homophobic, transphobic, religion, gender or racially motivated behaviour.'
WHERE question_code = 'history_of_hate_based_behaviour';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'For example, they are prominent on social media or are well-known in a particular area.'
WHERE question_code = 'high_profile_person';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Including any information relating to the individuals own needs or vulnerabilities.'
WHERE question_code = 'additional_rosh_info';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'If the person is subject to Multi-Agency Public Protection Arrangements, please outline level and category.'
WHERE question_code = 'mappa_nominal';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please detail any limitations on where the individual can undertake Community Payback work.'
WHERE question_code = 'location_exclusion_criteria';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Say whether there are any reasons for the individual to be made subject to more stringent supervision on Community Payback.'
WHERE question_code = 'restricted_placement';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please Outline reasons why this would not be suitable.'
WHERE question_code = 'no_female_supervisor';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please Outline reasons why this would not be suitable.'
WHERE question_code = 'no_male_supervisor';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Is the individual subject to a curfew or prohibited from undertaking any activity etc? Eg Non- Molestation Order, Sexual Harm Prevention Orders.'
WHERE question_code = 'restrictive_orders';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Individual placements are often in charity shops or other smaller organisations meaning the person will not be supervised by a member of probation staff. Give any reasons why this person should not work on a partner supervised placement or independent placement.'
WHERE question_code = 'risk_management_issues_individual';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'To help manage the dynamics of the group it''s helpful to avoid placing co-defendants, family members or anti-social peers on the same project. Please identify any person the individual should not be placed with.'
WHERE question_code = 'risk_management_issues_supervised_group';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Consider the risks if individuals who have a drug or alcohol dependency are required to use tools or machinery. It is important to make the Supervisor aware of any issues.'
WHERE question_code = 'alcohol_drug_issues';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = '<p class="govuk-hint">Outline nature of persons faith and check with them around possible adjustments such as prayer breaks or time off for significant festivals.</p><ul class="govuk-list govuk-list--bullet govuk-hint"><li>What is the persons faith/ belief</li><li>Does individuals'' faith and belief have any festivals that will affect attendance?</li><li>How will this effect attendance? Are there any adjustments required on groups to support the individual</li></ul>'
WHERE question_code = 'cultural_religious_adjustment';
UPDATE hmppsassessmentsschemas.question
SET question_text = 'Is the individual in Employment or education?', question_help_text = 'It is important to request evidence to verify this.'
WHERE question_code = 'employment_education';
UPDATE hmppsassessmentsschemas.question
SET question_text = 'Does the individual have an education, training or employment-related need? What types of courses would be applicable?'
WHERE question_code = 'education_training_need';
