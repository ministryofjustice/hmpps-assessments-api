INSERT INTO assessment_predictors (assessment_schema_code, predictor_type) VALUES
('RSR_ONLY', 'RSR')
ON CONFLICT DO NOTHING;

-- TODO: add actual mappings
INSERT INTO predictor_field_mapping (predictor_mapping_uuid, question_schema_uuid, predictor_type, predictor_field_name, required) VALUES
('c828f801-667f-48cf-8371-8c19aac28d95', '63099aab-f852-4dd9-9179-16ee2218d0c6', 'RSR', 'age_first_sanction', true)
ON CONFLICT DO NOTHING;
