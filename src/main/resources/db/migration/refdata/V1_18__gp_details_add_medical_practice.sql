-- Add gp_name and gp_practice_name questions
INSERT INTO question (question_uuid, question_code, question_start, question_end, answer_type, answer_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('6001121c-5b82-467f-bcfe-597f5e91e2f0', 'gp_practice_name', '2021-09-27 14:50:00', null, 'freetext', null, 'Medical practice', null, null),
    ('4b1e24af-6eb0-4d3c-add2-c6716878d480', 'gp_name', '2021-09-27 14:50:00', null, 'freetext', null, 'Name', null, null);
-- Add questions to the other_health_issues question group. NB: I've just used an arbitrary value for display order here as it's not used AFAIK
INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, read_only)
VALUES
    ('31947ec9-93fc-48f3-882e-3bdb06edc718', '6001121c-5b82-467f-bcfe-597f5e91e2f0', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 24, false),
    ('7d36a561-8138-423e-bcf4-29dfaa0bd690', '4b1e24af-6eb0-4d3c-add2-c6716878d480', 'question', 'dfa7b0e0-75a0-4117-ae0c-143c6ed97370', 25, false);