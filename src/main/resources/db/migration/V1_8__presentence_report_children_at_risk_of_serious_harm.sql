INSERT INTO answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES ('8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', 'male-female-notspecified-notknown', '2020-11-30 14:50:00', null),
    ('7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'neglect-physical-sexual-emotional', '2020-11-30 14:50:00', null);

INSERT INTO answer_schema (answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES ('d9169ca3-6b6a-4206-b909-48e3e10af0dc', 'male', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'male', 'Male'),
       ('051a5b80-03b6-4195-b9d3-5f03091e1f1b', 'female', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'female', 'Female'),
       ('76a3676b-a68e-41ae-8171-229cfd335f6f', 'not_specified', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'not specified', 'Not specified'),
       ('2bcb18ab-9310-4188-a3a5-7d262fbb5b43', 'not_known', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', '2020-11-30 14:50:00', null, 'not known', 'Not known'),
       ('d82a79b2-b6ff-44e8-a4ae-c4ad8312f48d', 'neglect', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'neglect', 'Neglect'),
       ('74865d30-f744-41ef-a7f2-eec481b2d4c6', 'physical', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'physical', 'Physical'),
       ('b4a070d2-33b4-40f3-a155-a25912c9c8c5', 'sexual', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'sexual', 'Sexual'),
       ('4fd4b523-01fc-4ac3-966b-5afcd56dca9b', 'emotional', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', '2020-11-30 14:50:00', null, 'emotional', 'Emotional');


INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('e641baa1-d3d6-4b31-8a7e-3ddc7e267d5f', 'psr_children_at_risk', 'psr-children-at-risk', null, null, '2020-11-30 14:50:00', null),
    ('fb777be0-a183-4c83-8209-e7871df9c547', 'children_at_risk_of_serious_harm', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);


INSERT INTO question_schema (question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('8c5c0274-3087-4ea3-aa09-4efd88a64490', 'uiS5.1', 'HEADING', '2020-11-30 14:50:00', null, 'presentation: inset', null, 'Consider carefully whether this information can be recorded safely.', null, null, null),
    ('1c7dc48a-e90f-4141-829e-ff16ce8dbb20', 'uiS5.2', null, '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Personal details', null, null, null),
    ('23c3e984-54c7-480f-b06c-7d000e2fb87c', '203.1', 'child_at_risk_pivot.name_of_child', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null),
    ('263c9dde-4290-4bf9-a596-f470b68da524', '204.1', 'child_at_risk_pivot.age_of_child', '2020-11-30 14:50:00', null, 'numeric', null, 'Age', 'Enter an age in years from 0 to 17', null, null),
    ('41677239-234c-4653-ac98-4e2e002948cc', '205.1', 'child_at_risk_pivot.date_of_birth', '2020-11-30 14:50:00', null, 'date', null, 'Date of Birth', null, null, null),
    ('61857dff-dfd8-4b92-a8c7-1ddfc4cebc45', '206.1', 'child_at_risk_pivot.gender_ELM', '2020-11-30 14:50:00', null, 'radio', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', 'Gender', null, null, null),
    ('3a0351a4-077e-4da0-b66d-bd552da6b273', 'uiS5.3', null, '2020-11-30 14:50:00', null, 'presentation: divider', null, null, null, null, null),
    ('4903acda-926d-4c7e-813f-7511bf5a62c4', 'uiS5.4', null, '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Contact details', null, null, null),
    ('c8668b01-ad96-454f-8afd-cccf6d827d5b', '207.1', 'child_at_risk_pivot.address_of_child', '2020-11-30 14:50:00', null, 'textarea', null, 'Address (if disclosable)', null, null, null),
    ('d52774aa-f2cb-457b-b494-62cf68fd9850', 'uiS5.5', null, '2020-11-30 14:50:00', null, 'presentation: divider', null, null, null, null, null),
    ('e874acf7-5515-48d0-9ee3-efb034b2f05b', 'uiS5.6', null, '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Social services details', null, null, null),
    ('ecb880ef-02c7-4ffc-8726-7e0f60f1191c', '208.1', 'child_at_risk_pivot.curreg_sclsrvs_ind', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Currently Registered with Social Services', null, null, null),
    ('775cd8bf-fe1b-4625-9937-ae8a238a8402', '209.1', 'child_at_risk_pivot.evr_prv_social_srv_ind', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Previously Registered with Social Services', null, null, null),
    ('589686ed-add7-4f36-a14d-80a4f450ae63', 'uiS5.7', null, '2020-11-30 14:50:00', null, 'presentation: divider', null, null, null, null, null),
    ('2faa8008-ecd2-4ba1-8064-ce5c3d1fa360', 'uiS5.8', null, '2020-11-30 14:50:00', null, 'presentation: heading_large', null, 'Child protection details', null, null, null),
    ('ad56e86d-56df-4c1a-974f-81454e941be0', '210.1', 'child_at_risk_reg_pivot_pivot.catg_cpr_ELM for current_previous_ELM = ‘Current’', '2020-11-30 14:50:00', null, 'checkbox', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'Current category of child protection registration', null, null, null),
    ('b0a4f119-6226-452b-a145-1f822d5737fe', '211.1', 'child_at_risk_reg_pivot_pivot.catg_cpr_ELM for current_previous_ELM = ‘Current’', '2020-11-30 14:50:00', null, 'checkbox', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'Previous category of child protection registration', null, null, null);


INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('35801191-cd3b-48fd-81a6-0517ab0e5d3a', 'fb777be0-a183-4c83-8209-e7871df9c547', 'group', '65a3924c-4130-4140-b7f4-cc39a52603bb', 1, true, null, false),
    ('38726e16-466a-4a27-93b3-92eb70f5dff7', '8c5c0274-3087-4ea3-aa09-4efd88a64490', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 1, true, null, true),
    ('73e7ee72-fb8b-4590-87f5-1ac40d2ae65c', '1c7dc48a-e90f-4141-829e-ff16ce8dbb20', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 2, true, null, true),
    ('c093a4ea-46a2-4b98-89cc-6bacaad4d401', '23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 3, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false),
    ('78a2c58c-ac43-4028-b6a9-a29f374694ee', '263c9dde-4290-4bf9-a596-f470b68da524', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 4, true, '{"mandatory":{"errorMessage":"Age cannot be more than 17","errorSummary":"Age cannot be more than 17"}}', false),
    ('bb71ade8-6704-4ac6-b81e-9b3d288bd9b6', '41677239-234c-4653-ac98-4e2e002948cc', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 5, true, '{"mandatory":{"errorMessage":"Enter date of birth","errorSummary":"Enter date of birth"}}', false),
    ('e37c5782-0f42-42a9-b914-886e8118e2cc', '61857dff-dfd8-4b92-a8c7-1ddfc4cebc45', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 6, true, '{"mandatory":{"errorMessage":"Select gender","errorSummary":"Select gender"}}', false),
    ('296545e4-b159-4d7f-b097-948e6fb6f30b', '3a0351a4-077e-4da0-b66d-bd552da6b273', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 7, true, null, true),
    ('1f4e29f7-41fd-4bfe-b8e0-b466ca055df1', '4903acda-926d-4c7e-813f-7511bf5a62c4', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 8, true, null, true),
    ('157ac842-0845-4593-bd78-bf8adb995f72', 'c8668b01-ad96-454f-8afd-cccf6d827d5b', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 9, true, '{"mandatory":{"errorMessage":"Enter address","errorSummary":"Enter address"}}', false),
    ('687ca1ea-a195-4b69-9f12-945b52cfb9b9', 'd52774aa-f2cb-457b-b494-62cf68fd9850', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 10, true, null, true),
    ('e100283f-84c5-4f6b-9697-51df8bb8eb2b', 'e874acf7-5515-48d0-9ee3-efb034b2f05b', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 11, true, null, true),
    ('2455e9ce-86cc-4734-a1ca-9be131aa805d', 'ecb880ef-02c7-4ffc-8726-7e0f60f1191c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 12, true, '{"mandatory":{"errorMessage":"Select yes or no","errorSummary":"Select yes or no"}}', false),
    ('ce4357d7-7bbc-46b0-af26-a1db7a1c5d42', '775cd8bf-fe1b-4625-9937-ae8a238a8402', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 13, true, '{"mandatory":{"errorMessage":"Select yes or no","errorSummary":"Select yes or no"}}', false),
    ('484426d3-8ba3-4f0b-b7df-13197421b078', '589686ed-add7-4f36-a14d-80a4f450ae63', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 14, true, null, true),
    ('3d40eee0-23c7-4207-b755-57ce01edd037', '2faa8008-ecd2-4ba1-8064-ce5c3d1fa360', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 15, true, null, true),
    ('270bff3e-f92d-4d0f-aa66-27d358b0d70e', 'ad56e86d-56df-4c1a-974f-81454e941be0', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 16, true, '{"mandatory":{"errorMessage":"Select a currentcategory of child protection registration","errorSummary":"Select a current category of child protection registration"}}', false),
    ('e799b9ff-03e4-430f-97ee-2aca8947d18f', 'b0a4f119-6226-452b-a145-1f822d5737fe', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 17, true, '{"mandatory":{"errorMessage":"Select a previous category of child protection registration","errorSummary":"Select a previous category of child protection registration"}}', false);
