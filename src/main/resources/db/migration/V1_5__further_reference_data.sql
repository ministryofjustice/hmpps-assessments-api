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

INSERT INTO grouping (group_id, group_uuid, heading, subheading, help_text)
VALUES  (201, '22222222-2222-2222-2222-222222222201', 'Layer 3', null, null),
        (202, '22222222-2222-2222-2222-222222222202', 'Case Identification', null, null),
        (203, '22222222-2222-2222-2222-222222222203', 'BCST', null, null),
        (204, '22222222-2222-2222-2222-222222222204', 'Case Identification', null, null);

