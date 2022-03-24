INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
('6001121c-5b82-467f-bcfe-597f5e91e2f0', 'gp_medical_practice', '2021-09-27 14:50:00', null, 'freetext', null, 'Medical practice', null, null)
;

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty, structured_question_code)
VALUES
('2a70c9d6-38b5-4879-bb19-c719f1d74f67', 'gp_medical_practice', 'UPW', 'DELIUS', '', 'structuredAnswer', 'secure/offenders/crn/$crn/personalContacts', null, false, 'gp_details')
;
