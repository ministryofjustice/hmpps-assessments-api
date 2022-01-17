ALTER TABLE external_source_question_mapping
ADD COLUMN parent_question_code VARCHAR(255) REFERENCES question_code;

UPDATE external_source_question_mapping
SET json_path_field = '$[?(@.relationshipType.code=='RT02' && @.active == true)]', field_type = 'table'
WHERE external_source_question_mapping_uuid = 'cb24dc64-e8fd-4960-8096-1907d59de3b0';

UPDATE external_source_question_mapping
SET json_path_field = '.firstName', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '57099ea6-37e0-441e-9d05-ca840d225261';

UPDATE external_source_question_mapping
SET json_path_field = '.surname', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = 'a9ec6f88-58ad-4339-8969-3c27a62c85db';

UPDATE external_source_question_mapping
SET json_path_field = '.address.buildingName', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '8b7c2dac-620a-4fd9-9f78-016ed53511a4';

UPDATE external_source_question_mapping
SET json_path_field = '.address.addressNumber', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '873c2901-6f23-4a7f-b230-fafba2819faf';

UPDATE external_source_question_mapping
SET json_path_field = '.address.streetName', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = 'b1c76706-8634-43c9-8c70-67b78976d672';

UPDATE external_source_question_mapping
SET json_path_field = '.address.district', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '4283f9e3-0ec5-4f40-831c-0c93160d5c43';

UPDATE external_source_question_mapping
SET json_path_field = '.address.town', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '7501f047-2836-4bb8-8e03-54ab40543a8b';

UPDATE external_source_question_mapping
SET json_path_field = '.address.county', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '926c91e1-d020-4b16-9040-acd9b0dc1d53';

UPDATE external_source_question_mapping
SET json_path_field = '.address.postcode', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '7762598e-dc3c-41a4-8546-38444c0ef708';

UPDATE external_source_question_mapping
SET json_path_field = '.address.telephoneNumber', parent_question_code = 'gp_details', field_type = 'table_question', external_source_endpoint = null
WHERE external_source_question_mapping_uuid = '7802580e-fa85-4998-966e-cc29e58f04a5';