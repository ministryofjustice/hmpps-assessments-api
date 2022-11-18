-- add modern day slavery perpetrator questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('53369961-408b-4586-9430-a5d75baf99f1', 'modern_day_slavery_risks_perpetrator', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there specific risks that need to be taken into account?', null, null),
    ('076759c8-31ba-4fae-9d03-15aacd20f5df', 'modern_day_slavery_risks_details_perpetrator', '2022-09-01 01:00:00', null, 'textarea', null, 'Give details', null, null),
    ('e50291a3-0203-49a4-9d22-5ad135168a48', 'modern_day_slavery_orders_perpetrator', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any slavery and trafficking prevention orders or slavery and trafficking risk orders in place?', null, null),
    ('32088224-d3e1-494e-8f5b-d48e7285352e', 'modern_day_slavery_orders_details_perpetrator', '2022-09-01 01:00:00', null, 'textarea', null, 'What restrictions do they specify?', null, null),
    ('481101a9-89cf-40ad-b5ee-a1eed362e990', 'modern_day_slavery_safeguarding_perpetrator', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any safeguarding measures that need to be put into place?', null, null),
    ('03c9820e-4ecf-4658-b837-122f0e82f40e', 'modern_day_slavery_safeguarding_details_perpetrator', '2022-09-01 01:00:00', null, 'textarea', null, 'Give details', null, null),
    ('1a694814-e547-45f5-8bd8-adc404db59af', 'modern_day_slavery_complete_perpetrator', '2022-09-01 01:00:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark modern day slavery section as complete?', '', null);

-- set up question dependencies
INSERT INTO question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES
    ('076759c8-31ba-4fae-9d03-15aacd20f5df', '53369961-408b-4586-9430-a5d75baf99f1', 'YES', '2022-09-01 01:00:00', true),
    ('32088224-d3e1-494e-8f5b-d48e7285352e', 'e50291a3-0203-49a4-9d22-5ad135168a48', 'YES', '2022-09-01 01:00:00', true),
    ('03c9820e-4ecf-4658-b837-122f0e82f40e', '481101a9-89cf-40ad-b5ee-a1eed362e990', 'YES', '2022-09-01 01:00:00', true);

-- create the modern day slavery question group
INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('5779c8c2-4ce0-40e8-8c46-d164effed425', 'modern_day_slavery_perpetrator', 'Modern day slavery - perpetrator', null, null, '2021-09-27 14:50:00', null);

-- add questions the modern day slavery question group  NB: I've just used an arbitrary value for display order here as it's not used AFAIK
INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('a3825031-cf51-4f36-b217-790130233df8', '5779c8c2-4ce0-40e8-8c46-d164effed425', 'group', '95000412-07cb-49aa-8821-6712880e3097', 4, true),
    ('972130e8-01c9-4665-b449-98d3e9613f17', '53369961-408b-4586-9430-a5d75baf99f1', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 1, false),
    ('274ce8ce-d672-452b-bf49-e999c963b234', '076759c8-31ba-4fae-9d03-15aacd20f5df', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 2, false),
    ('a51c385e-8d75-4a3e-8a74-b803d496acf8', 'e50291a3-0203-49a4-9d22-5ad135168a48', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 3, false),
    ('18be5c42-13c7-4f51-8d34-49227f159e47', '32088224-d3e1-494e-8f5b-d48e7285352e', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 4, false),
    ('0717752e-5638-423a-939a-5f17c101f64e', '481101a9-89cf-40ad-b5ee-a1eed362e990', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 5, false),
    ('3dc5b75a-ca3e-45fb-b021-a8c061f6d4a1', '03c9820e-4ecf-4658-b837-122f0e82f40e', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 6, false),
    ('931e7acd-473b-457a-b479-8be16c45c948', '1a694814-e547-45f5-8bd8-adc404db59af', 'question', '5779c8c2-4ce0-40e8-8c46-d164effed425', 7, false);

-- add modern day slavery victim questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('626961f7-756b-4076-be31-29cf9a70ab99', 'modern_day_slavery_risks_victim', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there specific risks that need to be taken into account?', null, null),
    ('b0ff8f51-84f0-4a21-86e6-730f7251291b', 'modern_day_slavery_risks_details_victim', '2022-09-01 01:00:00', null, 'textarea', null, 'Give details', null, null),
    ('7cb05959-8bc5-4c1a-a5ec-1850a58d5fca', 'modern_day_slavery_safeguarding_victim', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any safeguarding measures that need to be put into place?', null, null),
    ('adb14e5e-d14a-4b5f-b01c-11845ed58b45', 'modern_day_slavery_safeguarding_details_victim', '2022-09-01 01:00:00', null, 'textarea', null, 'Give details', null, null),
    ('14b1797f-f2e5-40a6-90bc-c7ee7b5594d8', 'modern_day_slavery_complete_victim', '2022-09-01 01:00:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark modern day slavery section as complete?', '', null);

-- set up question dependencies
INSERT INTO question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES
    ('b0ff8f51-84f0-4a21-86e6-730f7251291b', '626961f7-756b-4076-be31-29cf9a70ab99', 'YES', '2022-09-01 01:00:00', true),
    ('adb14e5e-d14a-4b5f-b01c-11845ed58b45', '7cb05959-8bc5-4c1a-a5ec-1850a58d5fca', 'YES', '2022-09-01 01:00:00', true);

-- create the modern day slavery question group
INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 'modern_day_slavery_victim', 'Modern day slavery - victim', null, null, '2021-09-27 14:50:00', null);

-- add questions the modern day slavery question group  NB: I've just used an arbitrary value for display order here as it's not used AFAIK
INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('3dac32ce-65e0-44a0-b62d-9c3160f12434', '1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 'group', '95000412-07cb-49aa-8821-6712880e3097', 4, true),
    ('7e218d75-d4c8-49a6-a5c4-e41f781ea1fc', '626961f7-756b-4076-be31-29cf9a70ab99', 'question', '1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 1, false),
    ('a50bf6ee-54f3-40c7-8740-5a94876e2424', 'b0ff8f51-84f0-4a21-86e6-730f7251291b', 'question', '1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 2, false),
    ('f7f0d6ee-68dd-416c-810a-eae770958c46', '7cb05959-8bc5-4c1a-a5ec-1850a58d5fca', 'question', '1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 3, false),
    ('6bc062c2-29bc-4161-85b9-308fdc13fe76', 'adb14e5e-d14a-4b5f-b01c-11845ed58b45', 'question', '1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 4, false),
    ('afe9817d-3c6e-493a-aa78-9ff425f12e60', '14b1797f-f2e5-40a6-90bc-c7ee7b5594d8', 'question', '1f2c67ad-fe31-4fa5-93dc-4dd7192d2dcd', 5, false);