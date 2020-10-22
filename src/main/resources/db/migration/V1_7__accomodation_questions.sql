INSERT INTO question_schema (question_schema_id, question_schema_uuid, question_code, oasys_question_code, question_start, question_end, answer_type, question_text, question_help_text)
VALUES  (231, '11111111-1111-1111-1111-111111111231', 'no_fixed_abode', 'OL3 3.3', '2019-11-14 08:11:53.177108', null, 'freetext', 'Currently of no fixed abode or in transient accomodation', null);

INSERT INTO grouping (group_id, group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES  (230, '22222222-2222-2222-2222-222222222230', 'oasys_accomodation', 'Accommodation', null, null, '2019-11-14 08:11:53.177108', null);

-- Layer 3 Case Identification
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (231, '33333333-3333-3333-3333-33333333331', '11111111-1111-1111-1111-111111111231', 'question', '22222222-2222-2222-2222-222222222230', '1', 'no', null);

-- Add to Layer 3
INSERT INTO question_group (question_group_id, question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation)
VALUES  (331, '33333333-3333-3333-3333-333333333330', '22222222-2222-2222-2222-222222222230', 'group', '22222222-2222-2222-2222-222222222201', '1', 'yes', null);
