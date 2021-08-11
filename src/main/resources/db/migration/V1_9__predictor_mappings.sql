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
('ef3f6c4a-c4d1-4fbe-82eb-3914b7519629', '420c2ffe-8f2c-49b3-a523-674af3197b9e', 'RSR', 'completed_interview', true),
('61d76f92-e603-4c91-9e75-73e48aca8664', 'ed0e988a-38a4-4f9f-9691-08fb695cbed9', 'RSR', 'suitable_accommodation', false),
('6a37b92f-f316-4e66-bb28-fc1c6d88b79b', '3211a668-8878-4e88-8457-8250bfe65aea', 'RSR', 'unemployed_on_release', false),
('b608c6fd-4b93-4581-886b-f5925f0d9b4d', '1970ba5e-91cb-4ad3-9f04-64d5b5b7157b', 'RSR', 'current_relationship_with_partner', false),
('89926828-7862-4b22-b02e-2e449a76c622', 'f04dd882-0a0d-49f5-9736-91eeadbff9e7', 'RSR', 'evidence_domestic_violence', false),
('ca66afba-46c0-4be4-a23d-306bce15a4fd', '38b3a40a-df23-4ea1-872e-c04a8b03ee05', 'RSR', 'perpetrator_domestic_violence', false),
('a4feacfe-6bda-41d6-806e-872c0b84189e', 'f0416e89-3a71-46d1-8fa2-aebd886dcb34', 'RSR', 'use_of_alcohol', false),
('8dea672f-5264-4973-94f4-9d10f1daf5f6', '574618c3-27f4-4dd2-94bb-6de74126ff229', 'RSR', 'binge_drinking', false),
('0365e911-b1bd-4271-b498-3657eda1c859', '5a90a38d-ee0a-4775-994c-addf3397b817', 'RSR', 'impulsivity_issues', false),
('42d9430a-f004-402c-af37-4b8bf24ef0fb', 'd0619e6b-cc90-4031-90c6-ab15e06cc779', 'RSR', 'temper_control_issues', false),
('c5d29ac7-69bb-49a6-bf3d-9943a0e93303', '2f1b543b-1e69-4f7e-a61c-6d21ff967432', 'RSR', 'pro_criminal_attitudes', false),
('8deaa2c7-a646-4d1f-bb18-30e2289d219c', '0941c5b2-f42d-4120-ad79-44954674fe00', 'RSR', 'previous_murder_attempt', false),
('1a51093d-8a55-4c32-9c01-0eacf6bd7c93', 'f988f76c-3d6c-4f45-aa29-7dc8d11198d7', 'RSR', 'previous_wounding', false),
('afb6afb7-4f48-432f-abf4-693710708534', 'ad81d270-4acc-472c-a79e-01d0a422ce80', 'RSR', 'previous_aggravated_burglary', false),
('524c4792-9aff-4447-bd59-fb3ecd9d80e5', 'f8789074-1532-4b32-8995-780da18e273a', 'RSR', 'previous_arson', false),
('23d67995-183a-48d1-98cc-729441b610d2', 'df5d635a-6765-42af-9007-6d6d333da5f2', 'RSR', 'previous_criminal_damage', false),
('28cc6b11-5653-4bf3-a515-89c6fd59a1c9', 'c2b221b4-ee1f-41c8-8fc7-9d49998fab35', 'RSR', 'previous_kidnapping', false),
('a1c0d8da-52a3-4539-a86e-632aea772c7a', 'e887bcea-91d1-4c50-a25e-4335fa1e6ae5', 'RSR', 'previous_possession_firearm', false),
('a7be30d2-987a-44f1-ac1c-9722dc803f20', '9692659a-778a-436a-bf4e-fe1924638e37', 'RSR', 'previous_robbery', false),
('cf0c913f-c5d5-4b28-a861-ac55b218899f', '68e31f3a-5175-47e2-986b-d722ad78d893', 'RSR', 'previous_offence_weapon', false)
ON CONFLICT DO NOTHING;
