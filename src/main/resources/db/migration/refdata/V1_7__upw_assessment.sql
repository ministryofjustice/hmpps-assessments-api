INSERT INTO assessment_schema (assessment_schema_uuid, assessment_schema_code, oasys_assessment_type, oasys_create_assessment_at, assessment_name)
VALUES
('9c318330-091a-4a97-842f-1146df9e9703', 'UPW', 'SOMETHING_IN_OASYS', 'END', 'Unpaid Work Assessment');

INSERT INTO grouping (group_uuid, group_code, heading, subheading, help_text, group_start, group_end)
VALUES ('149f1b41-dea4-464a-8ac9-6e95b9e4667c', 'upw', 'Unpaid Work', null, null, '2020-11-30 14:50:00', null),
       ('ccd1ae9f-fcd9-41eb-88d9-d28945ed79b4', 'upw_assessment', 'Unpaid Work Assessment', null, null, '2020-11-30 14:50:00', null);

INSERT INTO assessment_schema_groups(assessment_schema_uuid, group_uuid)
VALUES ('9c318330-091a-4a97-842f-1146df9e9703', '149f1b41-dea4-464a-8ac9-6e95b9e4667c');