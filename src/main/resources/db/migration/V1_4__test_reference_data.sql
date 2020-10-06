-- noinspection SqlResolveForFile

INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, question_text, question_help_text)
VALUES  (1, '11111111-1111-1111-1111-111111111111', 'SR15.1.1', 'SR15.1', '2019-11-14 08:11:53.177108', null, 'scale', 'Rating', null),
        (2, '11111111-1111-1111-1111-111111111112', 'SR15.1.2', 'SR15.2', '2019-11-14 08:11:53.177108', null, 'y/n', 'Critical', null),
        (3, '11111111-1111-1111-1111-111111111113', 'SR15.1.3', 'SR15.3', '2019-11-14 08:11:53.177108', null, 'freetext', 'Comment', null),
        (4, '11111111-1111-1111-1111-111111111114', 'SR18.1.1', 'SR15.1', '2019-11-14 08:11:53.177108', null, 'scale', 'Rating', null),
        (5, '11111111-1111-1111-1111-111111111115', 'SR18.1.2', 'SR15.2', '2019-11-14 08:11:53.177108', null, 'y/n', 'Critical', null),
        (6, '11111111-1111-1111-1111-111111111116', 'SR18.1.3', 'SR15.3', '2019-11-14 08:11:53.177108', null, 'freetext', 'Comment', null);

INSERT INTO answer_schema (answer_schema_id, answer_schema_uuid, answer_schema_code, question_schema_uuid, answer_start, answer_end, value, text)
VALUES  (1, '44444444-4444-4444-4444-444444444441', 'SR15.1.1.1','11111111-1111-1111-1111-111111111111', '2019-11-14 08:11:53.177108', null, '0', '0'),
        (2, '44444444-4444-4444-4444-444444444442', 'SR15.1.1.2','11111111-1111-1111-1111-111111111111', '2019-11-14 08:11:53.177108', null, '1', '1'),
        (3, '44444444-4444-4444-4444-444444444443', 'SR15.1.1.3','11111111-1111-1111-1111-111111111111', '2019-11-14 08:11:53.177108', null, '2', '2'),
        (4, '44444444-4444-4444-4444-444444444444', 'SR15.1.2.1','11111111-1111-1111-1111-111111111112', '2019-11-14 08:11:53.177108', null, 'Y', 'Yes'),
        (5, '44444444-4444-4444-4444-444444444445', 'SR15.1.2.2','11111111-1111-1111-1111-111111111112', '2019-11-14 08:11:53.177108', null, 'N', 'No'),
        (6, '44444444-4444-4444-4444-444444444446', 'SR15.1.3','11111111-1111-1111-1111-111111111113', '2019-11-14 08:11:53.177108', null, 'freetext', 'freetext'),
        (7, '44444444-4444-4444-4444-444444444447', 'SR18.1.1.1','11111111-1111-1111-1111-111111111114', '2019-11-14 08:11:53.177108', null, '0', '0'),
        (8, '44444444-4444-4444-4444-444444444448', 'SR18.1.1.2','11111111-1111-1111-1111-111111111114', '2019-11-14 08:11:53.177108', null, '1', '1'),
        (9, '44444444-4444-4444-4444-444444444449', 'SR18.1.1.3','11111111-1111-1111-1111-111111111114', '2019-11-14 08:11:53.177108', null, '2', '2'),
        (10, '44444444-4444-4444-4444-444444444410', 'SR18.1.2.1','11111111-1111-1111-1111-111111111115', '2019-11-14 08:11:53.177108', null, 'Y', 'Yes'),
        (11, '44444444-4444-4444-4444-444444444411', 'SR18.1.2.2','11111111-1111-1111-1111-111111111115', '2019-11-14 08:11:53.177108', null, 'N', 'No'),
        (12, '44444444-4444-4444-4444-444444444412', 'SR18.1.3','11111111-1111-1111-1111-111111111116', '2019-11-14 08:11:53.177108', null, 'freetext', 'freetext');


INSERT INTO grouping (group_id, group_uuid, heading, subheading, help_text)
VALUES  (1, '22222222-2222-2222-2222-222222222221', 'SARA', null, null),
        (2, '22222222-2222-2222-2222-222222222222', 'Criminal History', 'Past assault of family members', null),
        (3, '22222222-2222-2222-2222-222222222223', 'Criminal History', 'Past assault of strangers or acquaintances', null);

INSERT INTO question_group (question_group_id, question_group_uuid, question_schema_uuid, group_uuid, group_name, display_order, mandatory, validation, group_start, group_end)
VALUES  (1, '33333333-3333-3333-3333-333333333331', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', 'SR15', '1.1', 'mandatory', null, '2019-11-14 08:11:53.177108', null ),
        (2, '33333333-3333-3333-3333-333333333332', '11111111-1111-1111-1111-111111111112', '22222222-2222-2222-2222-222222222222', 'SR15', '1.2', 'mandatory', null, '2019-11-14 08:11:53.177108', null ),
        (3, '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111113', '22222222-2222-2222-2222-222222222222', 'SR15', '1.3', 'mandatory', null, '2019-11-14 08:11:53.177108', null ),
        (4, '33333333-3333-3333-3333-333333333334', '11111111-1111-1111-1111-111111111114', '22222222-2222-2222-2222-222222222223', 'SR18', '2.1', 'mandatory', null, '2019-11-14 08:11:53.177108', null ),
        (5, '33333333-3333-3333-3333-333333333335', '11111111-1111-1111-1111-111111111115', '22222222-2222-2222-2222-222222222223', 'SR18', '2.2', 'mandatory', null, '2019-11-14 08:11:53.177108', null ),
        (6, '33333333-3333-3333-3333-333333333336', '11111111-1111-1111-1111-111111111116', '22222222-2222-2222-2222-222222222223', 'SR18', '2.3', 'mandatory', null, '2019-11-14 08:11:53.177108', null );
