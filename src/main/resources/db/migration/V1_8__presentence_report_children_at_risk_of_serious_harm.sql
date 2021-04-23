INSERT INTO answer_schema_group (answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES ('8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', 'male-female-notspecified-notknown', '2020-11-30 14:50:00', null),
    ('7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'neglect-physical-sexual-emotional', '2020-11-30 14:50:00', null);



INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('e641baa1-d3d6-4b31-8a7e-3ddc7e267d5f', 'psr_children_at_risk', 'psr-children-at-risk', null, null, '2020-11-30 14:50:00', null),
    ('fb777be0-a183-4c83-8209-e7871df9c547', 'children_at_risk_of_serious_harm', 'Children at Risk of Serious Harm', null, null, '2020-11-30 14:50:00', null);


INSERT INTO question_schema (question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, external_source, reference_data_category)
VALUES ('b9ed154e-5b4e-43af-a405-2ae0f22f2ac6', '202.1', 'HEADING', '2020-11-30 14:50:00', null, 'freetext', null, 'Inset text: Consider carefully whether this information can be recorded safely.', null, null, null),
    ('23c3e984-54c7-480f-b06c-7d000e2fb87c', '203.1', 'child_at_risk_pivot.name_of_child', '2020-11-30 14:50:00', null, 'freetext', null, 'Name', null, null, null),
    ('263c9dde-4290-4bf9-a596-f470b68da524', '204.1', 'child_at_risk_pivot.age_of_child', '2020-11-30 14:50:00', null, 'numeric', null, 'Age', 'Enter an age in years from 0 to 17', null, null),
    ('41677239-234c-4653-ac98-4e2e002948cc', '205.1', 'child_at_risk_pivot.date_of_birth', '2020-11-30 14:50:00', null, 'date', null, 'Date of Birth', null, null, null),
    ('61857dff-dfd8-4b92-a8c7-1ddfc4cebc45', '206.1', 'child_at_risk_pivot.gender_ELM', '2020-11-30 14:50:00', null, 'radio', '8ad6d0ab-bb5e-4ead-8596-f670aa3aa55b', 'Gender', null, null, null),
    ('c4a648af-8a53-452d-b575-35697932531e', '207.1', 'child_at_risk_pivot.address_of_child', '2020-11-30 14:50:00', null, 'freetext', null, 'Address (if disclosable)', null, null, null),
    ('ecb880ef-02c7-4ffc-8726-7e0f60f1191c', '208.1', 'child_at_risk_pivot.curreg_sclsrvs_ind', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Currently Registered with Social Services', null, null, null),
    ('775cd8bf-fe1b-4625-9937-ae8a238a8402', '209.1', 'child_at_risk_pivot.evr_prv_social_srv_ind', '2020-11-30 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Previously Registered with Social Services', null, null, null),
    ('ad56e86d-56df-4c1a-974f-81454e941be0', '210.1', 'child_at_risk_reg_pivot_pivot.catg_cpr_ELM for current_previous_ELM = ‘Current’', '2020-11-30 14:50:00', null, 'checkbox', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'Current category of child protection registration', null, null, null),
    ('b0a4f119-6226-452b-a145-1f822d5737fe', '211.1', 'child_at_risk_reg_pivot_pivot.catg_cpr_ELM for current_previous_ELM = ‘Current’', '2020-11-30 14:50:00', null, 'checkbox', '7f0dfd25-4ea5-4ade-ad51-4cb1ac1966d9', 'Previous category of child protection registration', null, null, null);


INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('8f7abac8-1799-479b-b7c0-031cc17ef15e', 'fb777be0-a183-4c83-8209-e7871df9c547', 'group', 'e641baa1-d3d6-4b31-8a7e-3ddc7e267d5f', 1, true, null, false),
    ('c28acb96-7945-42d6-9247-3a3d1b4a3709', 'b9ed154e-5b4e-43af-a405-2ae0f22f2ac6', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 1, true, null, false),
    ('c093a4ea-46a2-4b98-89cc-6bacaad4d401', '23c3e984-54c7-480f-b06c-7d000e2fb87c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 2, true, '{"mandatory":{"errorMessage":"Enter name","errorSummary":"Enter name"}}', false),
    ('78a2c58c-ac43-4028-b6a9-a29f374694ee', '263c9dde-4290-4bf9-a596-f470b68da524', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 3, true, '{"mandatory":{"errorMessage":"Age cannot be more than 17","errorSummary":"Age cannot be more than 17"}}', false),
    ('bb71ade8-6704-4ac6-b81e-9b3d288bd9b6', '41677239-234c-4653-ac98-4e2e002948cc', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 4, true, '{"mandatory":{"errorMessage":"Enter date of birth","errorSummary":"Enter date of birth"}}', false),
    ('e37c5782-0f42-42a9-b914-886e8118e2cc', '61857dff-dfd8-4b92-a8c7-1ddfc4cebc45', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 5, true, '{"mandatory":{"errorMessage":"Select gender","errorSummary":"Select gender"}}', false),
    ('25a5d3a8-12e1-4e9c-a166-8e5648bfac36', 'c4a648af-8a53-452d-b575-35697932531e', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 6, true, '{"mandatory":{"errorMessage":"Enter address","errorSummary":"Enter address"}}', false),
    ('2455e9ce-86cc-4734-a1ca-9be131aa805d', 'ecb880ef-02c7-4ffc-8726-7e0f60f1191c', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 7, true, '{"mandatory":{"errorMessage":"Select yes or no","errorSummary":"Select yes or no"}}', false),
    ('ce4357d7-7bbc-46b0-af26-a1db7a1c5d42', '775cd8bf-fe1b-4625-9937-ae8a238a8402', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 8, true, '{"mandatory":{"errorMessage":"Select yes or no","errorSummary":"Select yes or no"}}', false),
    ('270bff3e-f92d-4d0f-aa66-27d358b0d70e', 'ad56e86d-56df-4c1a-974f-81454e941be0', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 9, true, '{"mandatory":{"errorMessage":"Select a currentcategory of child protection registration","errorSummary":"Select a current category of child protection registration"}}', false),
    ('e799b9ff-03e4-430f-97ee-2aca8947d18f', 'b0a4f119-6226-452b-a145-1f822d5737fe', 'question', 'fb777be0-a183-4c83-8209-e7871df9c547', 10, true, '{"mandatory":{"errorMessage":"Select a previous category of child protection registration","errorSummary":"Select a previous category of child protection registration"}}', false);




