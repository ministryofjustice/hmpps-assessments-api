INSERT INTO answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES ('8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', 'male-female-notspecified-notknown', '2020-11-30 14:50:00', null),
('7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'neglect-physical-sexual-emotional', '2020-11-30 14:50:00', null);


INSERT INTO answer_schema (answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES ('7b82e106-0f05-4c3a-9e59-3f5a319d3f88', 'male', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'male', 'Male'),
('d875a2f0-7363-403b-9f73-1ad46cf3c6c5', 'female', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'female', 'Female'),
('6d59eb7b-e263-4e65-a114-4f97ec4dcc32', 'not_specified', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'not specified', 'Not specified'),
('52792000-9ee6-4284-8222-d880e7ba6bea', 'not_known', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'not known', 'Not known'),
('d6348467-bfab-4e8f-9162-ad1adbc94b06', 'neglect', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'neglect', 'Neglect'),
('4a790b90-aa60-4554-9c4e-bf83ea0f7895', 'physical', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'physical', 'Physical'),
('8de6868a-76be-45a0-becc-263b2a4e088f', 'sexual', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'sexual', 'Sexual'),
('191b4513-be7e-4b13-9e56-543c328029d9', 'emotional', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'emotional', 'Emotional');


INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('3c7eb3ac-53a7-4f61-a81f-38e8068b372c', 'psr_children_at_risk', 'psr_children_at_risk', null, null, '2020-11-30 14:50:00', null),
('729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 'children_at_risk_of_serious_harm', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);


INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('c993ef11-7b97-4dd6-a811-455ce9d675b9', 'uiS5.1', '2020-11-30 14:50:00', null, 'presentation: inset', null, 'Consider carefully whether this information can be recorded safely.', null, null, null),
('1ed5bd12-b3fa-4e8e-9ade-302a7d9b331e', 'uiS5.2', '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Personal details', null, null, null),
('0e9ac8f9-7df4-43de-9a7f-756f3fc0e5b3', '203.1', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null),
('1e053af6-247e-4f7f-934b-ab419c208067', '204.1', '2020-11-30 14:50:00', null, 'numeric', null, 'Age', 'Enter an age in years from 0 to 17', null, null),
('43451da5-5587-404f-8e9e-a238177bdfcb', '205.1', '2020-11-30 14:50:00', null, 'date', null, 'Date of Birth', null, null, null),
('9aebb4d0-f17a-4e86-a765-8f2a40b69dfb', '206.1', '2020-11-30 14:50:00', null, 'radio', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', 'Gender', null, null, null),
('caf15de3-7f1a-4ba4-b4b4-0b1856ae88bc', 'uiS5.3', '2020-11-30 14:50:00', null, 'presentation: divider', null, null, null, null, null),
('1e774cb1-285b-40fd-8c3b-ddff8eaf45d6', 'uiS5.4', '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Contact details', null, null, null),
('194f2f7e-61f0-458b-babd-91cea23e80af', '207.1', '2020-11-30 14:50:00', null, 'textarea', null, 'Address (if disclosable)', null, null, null),
('a4ab05a3-8a18-49f1-a51e-50f310699768', 'uiS5.5', '2020-11-30 14:50:00', null, 'presentation: divider', null, null, null, null, null),
('073d4e8b-5eff-4690-9890-7f9fb4b0d146', 'uiS5.6', '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Social services details', null, null, null),
('454c634e-61c2-48ae-9cd1-b0c09a7e392e', '208.1', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Currently Registered with Social Services', null, null, null),
('90b22f9a-d2ef-435a-89f2-318b98e1a72a', '209.1', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Previously Registered with Social Services', null, null, null),
('f822b029-5907-4789-93e0-e52aac2acfac', 'uiS5.7', '2020-11-30 14:50:00', null, 'presentation: divider', null, null, null, null, null),
('1be1c40e-07d4-4842-8447-6cd2bfffa3bd', 'uiS5.8', '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Child protection details', null, null, null),
('32be7f8b-5973-48c4-9640-0fbd292f6ff8', '210.1', '2020-11-30 14:50:00', null, 'checkbox', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'Current category of child protection registration', null, null, null),
('070232a5-c01e-452f-a554-2898f0674a45', '211.1', '2020-11-30 14:50:00', null, 'checkbox', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'Previous category of child protection registration', null, null, null);


INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('c298bc71-7308-499f-9f16-b2aa59b85d16', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 'group', '3c7eb3ac-53a7-4f61-a81f-38e8068b372c', 1, true, null, false),
('26f74281-1990-46ce-92d8-869bb01933ce', 'c993ef11-7b97-4dd6-a811-455ce9d675b9', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 1, true, null, true),
('6b4b2b37-8b5d-46b8-80c6-13b5b1286209', '1ed5bd12-b3fa-4e8e-9ade-302a7d9b331e', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 2, true, null, true),
('591a77e6-6e4c-418b-b3aa-3c0780809880', '0e9ac8f9-7df4-43de-9a7f-756f3fc0e5b3', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 3, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false),
('b96f25d6-4157-4120-9790-19e9e1654dfe', '1e053af6-247e-4f7f-934b-ab419c208067', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 4, true, '{"mandatory":{"errorMessage":"Age cannot be more than 17","errorSummary":"Age cannot be more than 17"}}', false),
('b814bb12-a76b-4766-9d2a-b71ac7696017', '43451da5-5587-404f-8e9e-a238177bdfcb', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 5, true, '{"mandatory":{"errorMessage":"Enter date of birth","errorSummary":"Enter date of birth"}}', false),
('5aaf8734-ec04-4324-921e-a720e98160ba', '9aebb4d0-f17a-4e86-a765-8f2a40b69dfb', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 6, true, '{"mandatory":{"errorMessage":"Select gender","errorSummary":"Select gender"}}', false),
('8a756157-cb77-472b-a6d8-0bafa1975f27', 'caf15de3-7f1a-4ba4-b4b4-0b1856ae88bc', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 7, true, null, true),
('d6dcc808-6fc3-4267-a61b-e6a3937da1a9', '1e774cb1-285b-40fd-8c3b-ddff8eaf45d6', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 8, true, null, true),
('f9cf7e57-f4a0-4d2e-8d08-e6c58a6fc792', '194f2f7e-61f0-458b-babd-91cea23e80af', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 9, true, '{"mandatory":{"errorMessage":"Enter address","errorSummary":"Enter address"}}', false),
('d9fda65c-85b8-47fd-b3c1-289b8d8d9e2f', 'a4ab05a3-8a18-49f1-a51e-50f310699768', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 10, true, null, true),
('ed820204-d724-42fc-b701-8ebf314d9453', '073d4e8b-5eff-4690-9890-7f9fb4b0d146', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 11, true, null, true),
('a92ea440-075c-4080-b566-918c5cda1831', '454c634e-61c2-48ae-9cd1-b0c09a7e392e', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 12, true, '{"mandatory":{"errorMessage":"Select yes or no","errorSummary":"Select yes or no"}}', false),
('47247eef-5241-4849-8783-35ae758c6617', '90b22f9a-d2ef-435a-89f2-318b98e1a72a', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 13, true, '{"mandatory":{"errorMessage":"Select yes or no","errorSummary":"Select yes or no"}}', false),
('ef51a35a-c01b-48d2-85fc-2a7389411cec', 'f822b029-5907-4789-93e0-e52aac2acfac', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 14, true, null, true),
('1fe42053-11c5-4715-a0f4-4e850e3539aa', '1be1c40e-07d4-4842-8447-6cd2bfffa3bd', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 15, true, null, true),
('73f65cc9-2286-4358-a04a-5902a88d8a98', '32be7f8b-5973-48c4-9640-0fbd292f6ff8', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 16, true, '{"mandatory":{"errorMessage":"Select a currentcategory of child protection registration","errorSummary":"Select a current category of child protection registration"}}', false),
('2c054816-9cae-4ad7-814e-70494cc63a69', '070232a5-c01e-452f-a554-2898f0674a45', 'question', '729f0c2a-9bc1-4cbc-843d-c91bf69fe76c', 17, true, '{"mandatory":{"errorMessage":"Select a previous category of child protection registration","errorSummary":"Select a previous category of child protection registration"}}', false);





