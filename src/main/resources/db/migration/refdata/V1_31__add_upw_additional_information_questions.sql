-- add additional information questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('1ee0e121-ad38-4e94-8150-d43d517fff8e', 'additional_information', '2023-03-01 01:00:00', null, 'textarea', null, 'Can you provide any additional information that is relevant for this assessment?', 'This page uses speech recognition technology', null),
    ('7daf455c-84b1-49d3-a45d-f99b74a3cabb', 'additional_information_complete', '2023-03-01 01:00:00', null, 'radio', '8067ff6e-7400-4d1e-ae2a-87dee7e124ec', 'Mark additional information section as complete?', '', null);

-- create additional information question group
INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('5a65554b-5591-4a42-a91a-66cfd78b96d1', 'additional_information', 'Additional information', null, null, '2023-03-01 01:00:00', null);

-- add questions additional information question group NB: I've just used an arbitrary value for display order here as it's not used AFAIK
INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('3f5dec4a-cbac-4c0b-8b78-17a333131e87', '5a65554b-5591-4a42-a91a-66cfd78b96d1', 'group', 'b0238dcb-e12a-4d07-9986-7214139942d1', 4, true),
    ('e1181899-cea0-4dc9-9ac7-c36a731c018f', '1ee0e121-ad38-4e94-8150-d43d517fff8e', 'question', '5a65554b-5591-4a42-a91a-66cfd78b96d1', 1, false),
    ('13dd2192-58fa-497f-9fb4-fa43b09cfca0', '7daf455c-84b1-49d3-a45d-f99b74a3cabb', 'question', '5a65554b-5591-4a42-a91a-66cfd78b96d1', 2, false);
