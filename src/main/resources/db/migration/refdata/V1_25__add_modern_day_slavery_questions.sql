-- add modern day slavery questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('86b3e0c5-2731-49c0-a76a-9baaef7941c9', 'modern_day_slavery_risks', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there specific risks  that need to be taken into account?', null, null),
    ('e84ee6f1-231c-4d65-a02c-00e3a2eb29e9', 'modern_day_slavery_risks_details', '2022-09-01 01:00:00', null, 'textarea', null, 'Give details', null, null),
    ('36c51db6-1f9c-4257-9372-7c48cbaa4cef', 'modern_day_slavery_orders', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any slavery and trafficking prevention orders or slavery and trafficking risk orders in place?', null, null),
    ('0b5a97bd-9f60-4a78-9f43-ccc68cc1a6ec', 'modern_day_slavery_orders_details', '2022-09-01 01:00:00', null, 'textarea', null, 'What restrictions do they specify?', null, null),
    ('2223afec-9239-406b-93a3-52186af94f0a', 'modern_day_slavery_safeguarding', '2022-09-01 01:00:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Are there any safeguarding measures that need to be put into place?', null, null),
    ('935df25c-ac07-4260-a298-57ea1631dac7', 'modern_day_slavery_safeguarding_details', '2022-09-01 01:00:00', null, 'textarea', null, 'Give details', null, null),
    ('efd7bc3f-b9f4-46c6-a06f-f29f911c0138', 'modern_day_slavery_complete', '2022-09-01 01:00:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark modern day slavery section as complete?', '', null);

-- set up question dependencies
INSERT INTO question_dependency (subject_question_uuid, trigger_question_uuid, trigger_answer_value, dependency_start, display_inline)
VALUES
    ('e84ee6f1-231c-4d65-a02c-00e3a2eb29e9', '86b3e0c5-2731-49c0-a76a-9baaef7941c9', 'YES', '2022-09-01 01:00:00', true),
    ('0b5a97bd-9f60-4a78-9f43-ccc68cc1a6ec', '36c51db6-1f9c-4257-9372-7c48cbaa4cef', 'YES', '2022-09-01 01:00:00', true),
    ('935df25c-ac07-4260-a298-57ea1631dac7', '2223afec-9239-406b-93a3-52186af94f0a', 'YES', '2022-09-01 01:00:00', true);

-- create the modern day slavery question group
INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('a65345d2-e6dd-4e79-8266-b262e0deec97', 'modern_day_slavery', 'Modern day slavery', null, null, '2021-09-27 14:50:00', null);

-- add questions the modern day slavery question group  NB: I've just used an arbitrary value for display order here as it's not used AFAIK
INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('3b9d1f50-b085-4621-86a8-6a3d87907c9c', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 'group', '95000412-07cb-49aa-8821-6712880e3097', 3, true),
    ('c2a65206-73d9-4ecb-96b0-4c72b1de9dee', '86b3e0c5-2731-49c0-a76a-9baaef7941c9', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 1, false),
    ('fc170bf5-b57e-47c2-8abe-758b8d8cf3c7', 'e84ee6f1-231c-4d65-a02c-00e3a2eb29e9', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 2, false),
    ('d286e0ce-61d7-4ae6-89a5-13134c3ed9be', '36c51db6-1f9c-4257-9372-7c48cbaa4cef', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 3, false),
    ('39d716f9-b769-455b-b99b-ecac8b9bc7d9', '0b5a97bd-9f60-4a78-9f43-ccc68cc1a6ec', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 4, false),
    ('8a7a11bc-f620-4f00-a4b9-09d1f438d2a5', '2223afec-9239-406b-93a3-52186af94f0a', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 5, false),
    ('00c27350-d95f-4ef8-8a66-ca6d7f8701dc', '935df25c-ac07-4260-a298-57ea1631dac7', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 6, false),
    ('f6ce5e6f-5596-44f8-9037-209c441e4240', 'efd7bc3f-b9f4-46c6-a06f-f29f911c0138', 'question', 'a65345d2-e6dd-4e79-8266-b262e0deec97', 7, false);
