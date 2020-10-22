-- noinspection SqlResolveForFile
INSERT INTO answer_schema_group (answer_schema_group_id, answer_schema_group_uuid, answer_schema_group_code, group_start, group_end)
VALUES (1, '44444444-4444-4444-5555-444444444444', 'YesNo', '2019-11-14 08:11:53.177108', null),
       (2, '44444444-4444-4444-5555-444444444445', 'NoSomeSig', '2019-11-14 08:11:53.177108', null),
       (3, '44444444-4444-4444-5555-444444444446', 'Employment', '2019-11-14 08:11:53.177108', null);


INSERT INTO answer_schema (answer_schema_id, answer_schema_uuid, answer_schema_code, answer_schema_group_uuid, answer_start, answer_end, value, text)
VALUES  (4, '44444444-4444-4444-4444-444444444444', 'SR15.1.2.1','44444444-4444-4444-5555-444444444444', '2019-11-14 08:11:53.177108', null, 'Y', 'Yes'),
        (5, '44444444-4444-4444-4444-444444444445', 'SR15.1.2.2','44444444-4444-4444-5555-444444444444', '2019-11-14 08:11:53.177108', null, 'N', 'No'),
        (6, '44444444-4444-4444-4444-444444444446', 'no_problems','44444444-4444-4444-5555-444444444445', '2019-11-14 08:11:53.177108', null, 'no', 'No problems'),
        (7, '44444444-4444-4444-4444-444444444447', 'some_problems','44444444-4444-4444-5555-444444444445', '2019-11-14 08:11:53.177108', null, 'some', 'Some problems'),
        (8, '44444444-4444-4444-4444-444444444448', 'significant_problems','44444444-4444-4444-5555-444444444445', '2019-11-14 08:11:53.177108', null, 'significant', 'Significant problems'),
        (9, '44444444-4444-4444-4444-444444444449', 'employed','44444444-4444-4444-5555-444444444446', '2019-11-14 08:11:53.177108', null, 'employed', 'Employed'),
        (10, '44444444-4444-4444-4444-444444444450', 'unemployed','44444444-4444-4444-5555-444444444446', '2019-11-14 08:11:53.177108', null, 'unemployed', 'Unemployed'),
        (11, '44444444-4444-4444-4444-444444444451', 'unavailable','44444444-4444-4444-5555-444444444446', '2019-11-14 08:11:53.177108', null, 'unavailable', 'Unavailable for work');


INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text)
VALUES  (1, '11111111-1111-1111-1111-111111111111', 'SR15.1.1', 'SR15.1', '2019-11-14 08:11:53.177108', null, 'scale',
null, 'Rating', null),
        (2, '11111111-1111-1111-1111-111111111112', 'SR15.1.2', 'SR15.2', '2019-11-14 08:11:53.177108', null, 'y/n', '44444444-4444-4444-5555-444444444444',  'Critical', null),
        (3, '11111111-1111-1111-1111-111111111113', 'SR15.1.3', 'SR15.3', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Comment', null),
        (4, '11111111-1111-1111-1111-111111111114', 'SR18.1.1', 'SR15.1', '2019-11-14 08:11:53.177108', null, 'scale', null, 'Rating', null),
        (5, '11111111-1111-1111-1111-111111111115', 'SR18.1.2', 'SR15.2', '2019-11-14 08:11:53.177108', null, 'y/n', '44444444-4444-4444-5555-444444444444', 'Critical', null),
        (6, '11111111-1111-1111-1111-111111111116', 'SR18.1.3', 'SR15.3', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Comment', null);

INSERT INTO grouping (group_id, group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES  (1, '22222222-2222-2222-2222-222222222221', 'SARA', 'SARA', null, null, '2019-11-14 08:11:53.177108', null),
        (2, '22222222-2222-2222-2222-222222222222', 'SR15', 'Criminal History', 'Past assault of family members', null, '2019-11-14 08:11:53.177108', null),
        (3, '22222222-2222-2222-2222-222222222223', 'SR18', 'Criminal History', 'Past assault of strangers or acquaintances', null, '2019-11-14 08:11:53.177108', null);

INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (1, '33333333-3333-3333-3333-333333333331', '11111111-1111-1111-1111-111111111111', 'question', '22222222-2222-2222-2222-222222222222', '1.1', 'mandatory', null),
        (2, '33333333-3333-3333-3333-333333333332', '11111111-1111-1111-1111-111111111112', 'question','22222222-2222-2222-2222-222222222222', '1.2', 'mandatory', null),
        (3, '33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111113', 'question','22222222-2222-2222-2222-222222222222', '1.3', 'mandatory', null),
        (4, '33333333-3333-3333-3333-333333333334', '11111111-1111-1111-1111-111111111114', 'question','22222222-2222-2222-2222-222222222223', '2.1', 'mandatory', null),
        (5, '33333333-3333-3333-3333-333333333335', '11111111-1111-1111-1111-111111111115', 'question','22222222-2222-2222-2222-222222222223', '2.2', 'mandatory', null),
        (6, '33333333-3333-3333-3333-333333333336', '11111111-1111-1111-1111-111111111116', 'question','22222222-2222-2222-2222-222222222223', '2.3', 'mandatory', null);


INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text)
VALUES  (201, '11111111-1111-1111-1111-111111111201', 'surname', 'OL3 CI1', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Surname', null),
(202, '11111111-1111-1111-1111-111111111202', 'forename', 'OL3 CI2', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Forename', null),
(203, '11111111-1111-1111-1111-111111111203', 's_alias', 'OL3 CI3', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Surname Aliases', null),
(204, '11111111-1111-1111-1111-111111111204', 'f_alias', 'OL3 CI4', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Forename Aliases', null),
(205, '11111111-1111-1111-1111-111111111205', 'dob', 'OL3 CI5', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Date of Birth', null),
(206, '11111111-1111-1111-1111-111111111206', 'dob_alias', 'OL3 CI6', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Date of Birth (Aliases)', null),
(207, '11111111-1111-1111-1111-111111111207', 'gender', 'OL3 CI7', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Gender', null),
(208, '11111111-1111-1111-1111-111111111208', 'religion', 'OL3 CI8', '2019-11-14 08:11:53.177108', null, 'freetext', null, 'Religion', null);

-- Layer 3 Accommodation
INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text)
VALUES (231, '11111111-1111-1111-1111-111111111231', 'no_fixed_abode', 'OL3 3.3', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444444', 'Currently of no fixed abode or in transient accomodation', null),
(232, '11111111-1111-1111-1111-111111111232', 'accom_suitability', 'OL3 3.4', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Suitability of accommodation', null),
(233, '11111111-1111-1111-1111-111111111233', 'accom_permanence', 'OL3 3.5', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Permanence of accommodation', null),
(234, '11111111-1111-1111-1111-111111111234', 'accom_location', 'OL3 3.6', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Suitability of location of accommodation', null),
(235, '11111111-1111-1111-1111-111111111235', 'accom_issues', 'OL3 3.7', '2019-11-14 08:11:53.177108', null, 'textarea', null, 'Identify accommodation issues contributing to risk of offending and harm. Please include any positive factors', null),
(236, '11111111-1111-1111-1111-111111111236', 'accom_rosha', 'OL3 3.8', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444444', 'Accommodation issues linked to risk of serious harm, risks to the individual & other risks', null),
(237, '11111111-1111-1111-1111-111111111237', 'accom_offending', 'OL3 3.9', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444444', 'Accommodation issues linked to offending behaviour', null);

-- Layer 3 Employment
INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text)
VALUES (240, '11111111-1111-1111-1111-111111111240', 'employment', 'OL3 4.2', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444446', 'Is the person unemployed, or will be unemployed on release?', null),
(241, '11111111-1111-1111-1111-111111111241', 'em_history', 'OL3 4.3', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Employment history', null),
(242, '11111111-1111-1111-1111-111111111242', 'em_skills', 'OL3 4.4', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Work related skills', null),
(243, '11111111-1111-1111-1111-111111111243', 'em_attitude', 'OL3 4.5', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Attitudes to employment', null),
(244, '11111111-1111-1111-1111-111111111244', 'em_school', 'OL3 4.6', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'School attendance', null),
(245, '11111111-1111-1111-1111-111111111245', 'em_problems', 'OL3 4.7', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444445', 'Has problems with reading, writing, or numeracy', null),
(246, '11111111-1111-1111-1111-111111111246', 'em_issues', 'OL3 4.11', '2019-11-14 08:11:53.177108', null, 'textarea', null, 'Identity education, training, and employability issues contributing to risks of offending and harm. Please include any positive factors', null),
(247, '11111111-1111-1111-1111-111111111247', 'em_rosha', 'OL3 4.12', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444444', 'Education/training/employability issues linked to risk of serious harm, risks to the individual & other risks', null),
(248, '11111111-1111-1111-1111-111111111248', 'em_offending', 'OL3 4.13', '2019-11-14 08:11:53.177108', null, 'radio', '44444444-4444-4444-5555-444444444444', 'Education/training/employability issues linked to offending behaviour', null);

INSERT INTO grouping (group_id, group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES  (201, '22222222-2222-2222-2222-222222222201', 'oasys', 'Long Form', null, null, '2019-11-14 08:11:53.177108', null),
(202, '22222222-2222-2222-2222-222222222202', 'oasys_case_id', 'Case Identification', null, null, '2019-11-14 08:11:53.177108', null),
(203, '22222222-2222-2222-2222-222222222203', 'bcst', 'Brief Form', 'Basic Custody Screen Tool', null, '2019-11-14 08:11:53.177108', null),
(204, '22222222-2222-2222-2222-222222222204', 'bcst_case_id', 'Case Identification', null, 'Offender and sentence details', '2019-11-14 08:11:53.177108', null),
(230, '22222222-2222-2222-2222-222222222230', 'oasys_accomodation', 'Accommodation', null, null, '2019-11-14 08:11:53.177108', null),
(231, '22222222-2222-2222-2222-222222222231', 'oasys_employment', 'Employment, training, employment', null, null, '2019-11-14 08:11:53.177108', null);

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
VALUES (301, '33333333-3333-3333-3333-333333333301', '22222222-2222-2222-2222-222222222202', 'group', '22222222-2222-2222-2222-222222222201', '1', 'yes', null);

-- Accomodation
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES (231, '33333333-3333-3333-3333-333333333341', '11111111-1111-1111-1111-111111111231', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(232, '33333333-3333-3333-3333-333333333342', '11111111-1111-1111-1111-111111111232', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(233, '33333333-3333-3333-3333-333333333343', '11111111-1111-1111-1111-111111111233', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(234, '33333333-3333-3333-3333-333333333344', '11111111-1111-1111-1111-111111111234', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(235, '33333333-3333-3333-3333-333333333345', '11111111-1111-1111-1111-111111111235', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(236, '33333333-3333-3333-3333-333333333346', '11111111-1111-1111-1111-111111111236', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(237, '33333333-3333-3333-3333-333333333347', '11111111-1111-1111-1111-111111111237', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null),
(239, '33333333-3333-3333-3333-333333333340', '22222222-2222-2222-2222-222222222230', 'group', '22222222-2222-2222-2222-222222222201', '2', 'yes', null);

-- Employment
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES (240, '33333335-5333-3333-3333-333333333240', '11111111-1111-1111-1111-111111111240', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(241, '33333335-5333-3333-3333-333333333241', '11111111-1111-1111-1111-111111111241', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(242, '33333335-5333-3333-3333-333333333242', '11111111-1111-1111-1111-111111111242', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(243, '33333335-5333-3333-3333-333333333243', '11111111-1111-1111-1111-111111111243', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(244, '33333335-5333-3333-3333-333333333340', '11111111-1111-1111-1111-111111111244', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(245, '33333335-5333-3333-3333-333333333345', '11111111-1111-1111-1111-111111111245', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(246, '33333335-5333-3333-3333-333333333346', '11111111-1111-1111-1111-111111111246', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(247, '33333335-5333-3333-3333-333333333347', '11111111-1111-1111-1111-111111111247', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(248, '33333335-5333-3333-3333-333333333348', '11111111-1111-1111-1111-111111111248', 'question', '22222222-2222-2222-2222-222222222231', '1', 'no', null),
(249, '33333333-3333-3333-3333-333333333349', '22222222-2222-2222-2222-222222222231', 'group', '22222222-2222-2222-2222-222222222201', '3', 'yes', null);


-- BCST
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (302, '33333333-3333-3333-3333-333333333303', '22222222-2222-2222-2222-222222222204', 'group', '22222222-2222-2222-2222-222222222203', '1', 'yes', null);



