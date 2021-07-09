INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('b89429c8-9e3e-4989-b886-9caed4ed0a30', 'rsr_only', 'RSR Only', null, null, '2020-11-30 14:50:00', null)
ON CONFLICT DO NOTHING;

INSERT INTO assessment_schema_groups(assessment_schema_group_id, assessment_schema_uuid, group_uuid)
VALUES (1, 'c3a6beac-37c0-46b6-b4b3-62086b624675', 'b89429c8-9e3e-4989-b886-9caed4ed0a30')
ON CONFLICT DO NOTHING;

INSERT INTO question_group (question_group_uuid, content_uuid, content_type, group_uuid, display_order, mandatory, validation, read_only)
VALUES ('26f59592-40c8-4f25-8dbc-9f9ca013b8c5', 'eb7b7324-f2a6-4902-91ef-709a8fab1f82', 'group', 'b89429c8-9e3e-4989-b886-9caed4ed0a30', 1, false, null, false)
ON CONFLICT DO NOTHING;
