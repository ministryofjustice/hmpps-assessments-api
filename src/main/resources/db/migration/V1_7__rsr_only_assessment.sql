INSERT INTO assessment_schema (assessment_schema_id, assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
(1, 'c3a6beac-37c0-46b6-b4b3-62086b624675', 'RSR', 'SOMETHING_IN_OASYS', 'END', 'RSR only Assessment')
ON CONFLICT DO NOTHING;


