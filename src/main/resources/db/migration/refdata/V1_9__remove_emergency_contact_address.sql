DELETE FROM question_group WHERE content_uuid IN (
    '9a2a0e8d-2e1c-45e6-84e8-e05c7d4f4b3a', -- emergency_contact_address_building_name
    '4e5d9a55-36da-4063-9188-40e3f6e324de', -- emergency_contact_address_house_number
    '242951c6-1b11-4b9e-b86c-b30aa550f2a8', -- emergency_contact_address_street_name
    'b798aafb-c90c-4477-801f-78648a35b537', -- emergency_contact_address_district
    '3d7c78eb-5fe9-42ca-9353-e0e4722f97d9', -- emergency_contact_address_town_or_city
    '3b6e2dcc-311c-46ed-8bbf-7076cf1a704c', -- emergency_contact_address_county
    'ea4f4c75-f39f-4f4a-8bf8-5babc5c2ed2b'  -- emergency_contact_address_postcode
);

DELETE FROM external_source_question_mapping WHERE question_code IN (
    'emergency_contact_address_building_name',
    'emergency_contact_address_house_number',
    'emergency_contact_address_street_name',
    'emergency_contact_address_district',
    'emergency_contact_address_town_or_city',
    'emergency_contact_address_county',
    'emergency_contact_address_postcode'
);

DELETE FROM question_schema WHERE question_schema_uuid IN (
   '9a2a0e8d-2e1c-45e6-84e8-e05c7d4f4b3a', -- emergency_contact_address_building_name
   '4e5d9a55-36da-4063-9188-40e3f6e324de', -- emergency_contact_address_house_number
   '242951c6-1b11-4b9e-b86c-b30aa550f2a8', -- emergency_contact_address_street_name
   'b798aafb-c90c-4477-801f-78648a35b537', -- emergency_contact_address_district
   '3d7c78eb-5fe9-42ca-9353-e0e4722f97d9', -- emergency_contact_address_town_or_city
   '3b6e2dcc-311c-46ed-8bbf-7076cf1a704c', -- emergency_contact_address_county
   'ea4f4c75-f39f-4f4a-8bf8-5babc5c2ed2b'  -- emergency_contact_address_postcode
);

-- Update question positions for questions after the removed ones
UPDATE question_group SET display_order = 21 WHERE content_uuid = '19c1ef38-d5d9-4d0a-8b45-ee344fd1b56d';
UPDATE question_group SET display_order = 22 WHERE content_uuid = '07334e88-94ab-4121-ad51-1b9ae050a3cd';
UPDATE question_group SET display_order = 23 WHERE content_uuid = 'c8a72834-64e9-41dd-a23d-52ea0f60ed64';
