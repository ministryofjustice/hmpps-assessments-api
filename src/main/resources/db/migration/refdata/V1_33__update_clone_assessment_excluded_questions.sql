INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('54d6f018-6433-4bc4-b7b8-e1f8d9f51991', 'current_disabilities', '2023-06-01 00:00:00', null, 'freetext', null, 'Disabilities', '', null),
    ('a240fbd0-39bb-457f-8dcd-1baa22848fcc', 'current_provisions', '2023-06-01 00:00:00', null, 'freetext', null, 'Provisions', '', null);

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('e1bd9d8b-9717-4426-add0-43013148cd19', '54d6f018-6433-4bc4-b7b8-e1f8d9f51991', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 15, true),
    ('c36ad317-62dd-49df-b6e0-f89bee40e7e2', 'a240fbd0-39bb-457f-8dcd-1baa22848fcc', 'question', '6f8bcf8b-bf9c-4410-9111-07d94d32864a', 16, true);

INSERT INTO clone_assessment_excluded_questions(assessment_type, question_code)
VALUES ('UPW', 'current_disabilities'),
       ('UPW', 'current_provisions'),
       ('UPW', 'active_disabilities');