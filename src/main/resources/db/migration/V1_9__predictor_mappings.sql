INSERT INTO assessment_predictors (assessment_schema_code, predictor_type) VALUES
('RSR', 'RSR')
ON CONFLICT DO NOTHING;

-- TODO: add actual mappings
INSERT INTO predictor_field_mapping (predictor_mapping_uuid, question_schema_uuid, predictor_type, predictor_field_name, required) VALUES
('c828f801-667f-48cf-8371-8c19aac28d95', '5ca86a06-5472-4861-bd6a-a011780db49a', 'RSR', 'date_first_sanction', true),
('d4e41828-9f40-4a1c-9a3d-121abe140989', '8e83a0ad-2fcf-4afb-a0af-09d1e23d3c33', 'RSR', 'total_sanctions', true),
('2ec91c12-90eb-4cf6-b17b-3cb35b69f55a', '496587b9-81f3-47ad-a41e-77900fdca573', 'RSR', 'total_violent_offences', true),
('7462816f-12d0-4465-9a4b-00e1b1fb629e', 'f5d1dd7c-1774-4c76-89c2-a47240ad98ba', 'RSR', 'date_current_conviction', true),
('521bff0e-93b1-44e1-a472-867b9bd90820', '58d3efd1-65a1-439b-952f-b2826ffa5e71', 'RSR', 'any_sexual_offences', true),
('a4634b3d-60bb-463d-93f1-25168f84232d', '3662710d-ce3e-4e45-bce3-caa4155872aa', 'RSR', 'current_sexual_offence', true),
('5bfd067f-67d0-419d-b6e9-db06532f5c1e', '86ee742c-4bfb-4e29-afca-04ad35a3abda', 'RSR', 'current_offence_victim_stranger', true),
('31418715-5ea6-422a-923a-d32d03630bc3', 'a00223d0-1c20-43b5-8076-8a292ca25773', 'RSR', 'most_recent_sexual_offence_date', true),
('32072397-d849-4708-8281-9feb00893c9f', 'fc45b061-a4a6-44c3-937c-2949069e0926', 'RSR', 'total_sexual_offences_adult', true),
('131fa3c0-119d-432a-a9ab-41f46006199f', 'ed495c57-21f3-4388-87e6-57017a6999b1', 'RSR', 'total_sexual_offences_child', true),
('cc7190e6-5611-4192-ac3b-17f9e8cb8cc6', '00a559e4-32d5-4ae7-aa21-247068a639ad', 'RSR', 'total_sexual_offences_child_image', true),
('e49598a5-5b10-408b-8fac-a0a29c6a3b83', '1b6c8f79-0fd9-45d4-ba50-309c3ccfdb2d', 'RSR', 'total_non_sexual_offences', true),
('c55c3087-c8b1-497c-929b-c1d107c37234', '5cd344d4-acf3-45a9-9493-5dda5aa9dfa8', 'RSR', 'earliest_release_date', true),
('ef3f6c4a-c4d1-4fbe-82eb-3914b7519629', '420c2ffe-8f2c-49b3-a523-674af3197b9e', 'RSR', 'completed_interview', true)
ON CONFLICT DO NOTHING;
