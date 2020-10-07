-- noinspection SqlResolveForFile

INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, question_text, question_help_text)
VALUES  (201, '11111111-1111-1111-1111-111111111201', 'surname', 'OL3 CI1', '2019-11-14 08:11:53.177108', null, 'freetext', 'Surname', null),
        (202, '11111111-1111-1111-1111-111111111202', 'forename', 'OL3 CI2', '2019-11-14 08:11:53.177108', null, 'freetext', 'Forename', null),
        (203, '11111111-1111-1111-1111-111111111203', 's_alias', 'OL3 CI3', '2019-11-14 08:11:53.177108', null, 'freetext', 'Surname Aliases', null),
        (204, '11111111-1111-1111-1111-111111111204', 'f_alias', 'OL3 CI4', '2019-11-14 08:11:53.177108', null, 'freetext', 'Forename Aliases', null),
        (205, '11111111-1111-1111-1111-111111111205', 'dob', 'OL3 CI5', '2019-11-14 08:11:53.177108', null, 'freetext', 'Date of Birth', null),
        (206, '11111111-1111-1111-1111-111111111206', 'dob_alias', 'OL3 CI6', '2019-11-14 08:11:53.177108', null, 'freetext', 'Date of Birth (Aliases)', null),
        (207, '11111111-1111-1111-1111-111111111207', 'gender', 'OL3 CI7', '2019-11-14 08:11:53.177108', null, 'freetext', 'Gender', null),
        (208, '11111111-1111-1111-1111-111111111208', 'religion', 'OL3 CI8', '2019-11-14 08:11:53.177108', null, 'freetext', 'Religion', null);

INSERT INTO grouping (group_id, group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES  (201, '22222222-2222-2222-2222-222222222201', 'oasys', 'Layer 3', null, null, '2019-11-14 08:11:53.177108', null),
        (202, '22222222-2222-2222-2222-222222222202', 'oasys_case_id', 'Case Identification', null, null, '2019-11-14 08:11:53.177108', null),
        (203, '22222222-2222-2222-2222-222222222203', 'bcst', 'BCST', 'Basic Custody Screen Tool', null, '2019-11-14 08:11:53.177108', null),
        (204, '22222222-2222-2222-2222-222222222204', 'bcst_case_id', 'Case Identification', null, 'Offender and sentence details', '2019-11-14 08:11:53.177108', null);

-- Layer 3 Case Identification
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (201, '33333333-3333-3333-3333-333333333201', '11111111-1111-1111-1111-111111111201', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (202, '33333333-3333-3333-3333-333333333202', '11111111-1111-1111-1111-111111111202', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (203, '33333333-3333-3333-3333-333333333203', '11111111-1111-1111-1111-111111111203', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (204, '33333333-3333-3333-3333-333333333204', '11111111-1111-1111-1111-111111111204', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (205, '33333333-3333-3333-3333-333333333205', '11111111-1111-1111-1111-111111111205', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (206, '33333333-3333-3333-3333-333333333206', '11111111-1111-1111-1111-111111111206', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (207, '33333333-3333-3333-3333-333333333207', '11111111-1111-1111-1111-111111111207', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null),
        (208, '33333333-3333-3333-3333-333333333208', '11111111-1111-1111-1111-111111111208', 'question', '22222222-2222-2222-2222-222222222202', '1', 'no', null);

-- BCST Case Identification
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (209, '33333333-3333-3333-3333-333333333209', '11111111-1111-1111-1111-111111111201', 'question', '22222222-2222-2222-2222-222222222204', '1', 'no', null),
        (210, '33333333-3333-3333-3333-333333333210', '11111111-1111-1111-1111-111111111202', 'question', '22222222-2222-2222-2222-222222222204', '1', 'no', null),
        (211, '33333333-3333-3333-3333-333333333211', '11111111-1111-1111-1111-111111111205', 'question', '22222222-2222-2222-2222-222222222204', '1', 'no', null),
        (212, '33333333-3333-3333-3333-333333333212', '11111111-1111-1111-1111-111111111207', 'question', '22222222-2222-2222-2222-222222222204', '1', 'no', null);

-- Layer 3
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (301, '33333333-3333-3333-3333-333333333301', '22222222-2222-2222-2222-222222222202', 'group', '22222222-2222-2222-2222-222222222201', '1', 'yes', null);
