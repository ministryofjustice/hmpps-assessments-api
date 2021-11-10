CREATE TABLE IF NOT EXISTS external_source_question_mapping(
    external_source_question_mapping_id     SERIAL      PRIMARY KEY,
    external_source_question_mapping_uuid   UUID        NOT NULL UNIQUE,
    question_code                           VARCHAR(255) NOT NULL,
    assessment_schema_code                  VARCHAR(50) NOT NULL,
    external_source                         VARCHAR(50) NOT NULL,
    json_path_field                         VARCHAR(1024) NOT NULL,
    field_type                              VARCHAR(50),
    external_source_endpoint                VARCHAR(1024),
    mapped_value                            VARCHAR(50),
    if_empty                                BOOLEAN DEFAULT false,
    FOREIGN KEY (assessment_schema_code) REFERENCES assessment_schema (assessment_schema_code),
    FOREIGN KEY (question_code) REFERENCES question_schema (question_code)
);

INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
('4e0dd4c4-d2c0-4701-93a8-65ed879aa575', 'family_name', 'ROSH', 'COURT', 'name.surname', 'varchar', null, null, false),
('e872d57c-e501-4fd4-ae70-7e9dba9551ce', 'first_name', 'ROSH', 'COURT', 'name.forename1', 'varchar', null, null, false),
('a93464c2-1828-46ea-ad5d-5d4e6accbe23', 'dob', 'ROSH', 'COURT', 'defendantDob', 'date', null, null, false),
('79cd9f1b-e06f-46ea-8125-8b29ec9c1e93', 'pnc', 'ROSH', 'COURT', 'pnc', 'varchar', null, null, false),
('b345f7bb-7398-4096-9802-c2599d762676', 'crn', 'ROSH', 'COURT', 'crn', 'varchar', null, null, false),
('a086610a-2135-40a4-ac88-da0534aee800', 'address_line_1', 'ROSH', 'COURT', 'defendantAddress.line1', 'varchar', null, null, false),
--UPW
('ba5d21bb-ee85-496b-9148-7062a925d384', 'first_name', 'UPW', 'DELIUS', 'firstName', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('e28fb88c-66ae-405e-9370-736e4225ff4a', 'first_name_aliases', 'UPW', 'DELIUS', '$.offenderAliases[*].[''firstName''])', 'array', 'secure/offenders/crn/$crn/all', null, false),
('e8e7e586-660f-40e8-a5cf-c78870efab41', 'family_name', 'UPW', 'DELIUS', 'surname', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('14796ba9-8f3a-48ee-b8b0-87f36b5c2e9e', 'family_name_aliases', 'UPW', 'DELIUS', '$.offenderAliases[*].[''surname''])', 'array', 'secure/offenders/crn/$crn/all', null, false),
('c8e38291-dbc1-432e-a248-94f2a8bd653a', 'dob', 'UPW', 'DELIUS', 'dateOfBirth', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('f9916f57-d398-43f1-a14d-9a5796d766d2', 'dob_aliases', 'UPW', 'DELIUS', '$.offenderAliases[*].[''dateOfBirth''])', 'array', 'secure/offenders/crn/$crn/all', null, false),
('5abb0441-57ee-4049-ad52-b4c5d34750dc', 'contact_address_building_name', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].buildingName', null, 'secure/offenders/crn/$crn/all', null, false),
('3da78bdf-b50e-4e1c-b762-8dc6231d9ec4', 'contact_address_house_number', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].addressNumber', null, 'secure/offenders/crn/$crn/all', null, false),
('7c97401a-51ef-4ea6-abf7-ab4afebba592', 'contact_address_street_name', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].streetName', null, 'secure/offenders/crn/$crn/all', null, false),
('48a72e06-7115-4ba3-978a-53350d0610af', 'contact_address_district', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].district', null, 'secure/offenders/crn/$crn/all', null, false),
('390135d2-9bc1-4b78-9f87-09a450c4cb48', 'contact_address_town_or_city', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].town', null, 'secure/offenders/crn/$crn/all', null, false),
('a0a3b08b-0084-44a4-b7df-380542337b83', 'contact_address_county', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].county', null, 'secure/offenders/crn/$crn/all', null, false),
('cc315d63-ddfa-4959-bb68-fd9a4be08a24', 'contact_address_postcode', 'UPW', 'DELIUS', '$.contactDetails.addresses[?(@.status.code==''M'')].postcode', null, 'secure/offenders/crn/$crn/all', null, false),
('7b21ad52-a2b5-4ef2-aa11-bebb4f7ac52a', 'crn', 'UPW', 'DELIUS', 'otherIds.crn', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('861bb639-61cf-4663-a2ef-55851135e8c5', 'pnc', 'UPW', 'DELIUS', 'otherIds.pncNumber', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('2ee48b0b-3677-4a97-ab77-27d99053ef61', 'ethnicity', 'UPW', 'DELIUS', 'offenderProfile.ethnicity', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('ebc6c624-eec2-48c8-86a2-6fe5bb52b84d', 'gender_identity', 'UPW', 'DELIUS', 'gender', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('6638e173-9ab9-4078-a6cc-2af97ae7dbde', 'language', 'UPW', 'DELIUS', '$.offenderProfile.offenderLanguages.primaryLanguage', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('406e1725-2e8c-411e-972b-f8661f4ce539', 'requires_interpreter', 'UPW', 'DELIUS', '$.offenderProfile.offenderLanguages.requiresInterpreter', 'varchar', 'secure/offenders/crn/$crn/all', null, false),
('7a129e3a-fe40-4138-9832-643404982a2c', 'contact_email_addresses', 'UPW', 'DELIUS', '$.contactDetails.emailAddresses[*]', 'array', 'secure/offenders/crn/$crn/all', null, false),
('4eb108ac-bbb7-416b-8d05-64283bdc6df5', 'contact_mobile_phone_number', 'UPW', 'DELIUS', '$.contactDetails.phoneNumbers[?(@.type==''MOBILE'')].number', 'array', 'secure/offenders/crn/$crn/all', null, false),
('be7794a5-1025-4ee5-bf36-c49fa5cc2acf', 'contact_phone_number', 'UPW', 'DELIUS', '$.contactDetails.phoneNumbers[?(@.type==''TELEPHONE'')].number', 'array', 'secure/offenders/crn/$crn/all', null, false),
('0a9efa70-9f42-41ff-96da-a814bed4befe', 'upw_physical_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D02'' || @.disabilityType.code==''RM'' || @.disabilityType.code==''RC'' || @.disabilityType.code==''PC'' || @.disabilityType.code==''VI'' || @.disabilityType.code==''HD'')].disabilityType.code', 'array', 'secure/offenders/crn/$crn/all', null, false),
('a9258dbd-9b16-4511-95c3-52aa7a95f948', 'upw_physical_disability_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D02'' || @.disabilityType.code==''RM'' || @.disabilityType.code==''RC'' || @.disabilityType.code==''PC'' || @.disabilityType.code==''VI'' || @.disabilityType.code==''HD'')].disabilityType.description', 'array', 'secure/offenders/crn/$crn/all', null, false),
('d6920b7b-927e-407a-b96c-a738d92b0bee', 'upw_learning_disability', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LA'')].disabilityType.code', 'array', 'secure/offenders/crn/$crn/all', null, false),
('27a0cc22-a876-4123-9a34-56363d4ce24f', 'upw_learning_disability_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LA'')].disabilityType.description', 'array', 'secure/offenders/crn/$crn/all', null, false),
('7cad64f6-898d-4e2f-a466-6d660b76380b', 'upw_learning_difficulty', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LD'')].disabilityType.code', 'array', 'secure/offenders/crn/$crn/all', null, false),
('0a8f5e55-1223-4912-94a7-7fad8dc28368', 'upw_learning_difficulty_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''LD'')].disabilityType.description', 'array', 'secure/offenders/crn/$crn/all', null, false),
('12be7d28-b20e-43f3-ba3b-3d0fd302e63a', 'upw_mental_health_condition', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D01'' || @.disabilityType.code==''MI'')].disabilityType.code', 'array', 'secure/offenders/crn/$crn/all', null, false),
('031e620a-b49a-4371-931b-6d6e05c3af4d', 'upw_mental_health_condition_details', 'UPW', 'DELIUS', '$.offenderProfile.disabilities[?(@.disabilityType.code==''D''|| @.disabilityType.code==''D01'' || @.disabilityType.code==''MI'')].disabilityType.description', 'array', 'secure/offenders/crn/$crn/all', null, false),

('8e219d36-6979-4018-8944-12777990910e', 'emergency_contact_first_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].firstName', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('46d7ffe0-705e-4027-95ed-359381b2f0d0', 'emergency_contact_family_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].surname', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('9bad074d-bfe0-4bc3-ba2e-0a5573b5d174', 'emergency_contact_relationship', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].relationship', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('c0483847-1e87-4061-a7bb-a44f615a0881', 'emergency_contact_mobile_phone_number', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].mobileNumber', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('ef0a997e-66d5-40f4-958c-b4ed84c9807e', 'emergency_contact_address_building_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.buildingName', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('0645aed0-fe8d-49ea-89d7-c5fb167ceef9', 'emergency_contact_address_house_number', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.addressNumber', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('d19f2e04-d452-41a4-87b0-de7cef7e9fe9', 'emergency_contact_address_street_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.streetName', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('61801492-1b02-40ad-9a05-b8602e7bbc5a', 'emergency_contact_address_district', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.district', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('86d4a8e6-f51b-49c2-9d01-5f452729f752', 'emergency_contact_address_town_or_city', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.town', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('c635434d-4c24-4780-995d-a7d44a55dada', 'emergency_contact_address_county', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.county', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('6f8bd761-dc11-4c72-b2ab-5e1ee8a0038f', 'emergency_contact_address_postcode', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.postcode', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('f7318c93-0929-4a10-bd6a-1f7e4674dc11', 'emergency_contact_phone_number', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''ME'')].address.telephoneNumber', null, 'secure/offenders/crn/$crn/personalContacts', null, false),

('57099ea6-37e0-441e-9d05-ca840d225261', 'upw_gp_first_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].firstName', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('a9ec6f88-58ad-4339-8969-3c27a62c85db', 'upw_gp_family_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].surname', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('8b7c2dac-620a-4fd9-9f78-016ed53511a4', 'upw_gp_address_building_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.buildingName', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('873c2901-6f23-4a7f-b230-fafba2819faf', 'upw_gp_address_house_number', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.addressNumber', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('b1c76706-8634-43c9-8c70-67b78976d672', 'upw_gp_address_street_name', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.streetName', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('4283f9e3-0ec5-4f40-831c-0c93160d5c43', 'upw_gp_address_district', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.district', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('7501f047-2836-4bb8-8e03-54ab40543a8b', 'upw_gp_address_town_or_city', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.town', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('926c91e1-d020-4b16-9040-acd9b0dc1d53', 'upw_gp_address_county', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.county', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('7762598e-dc3c-41a4-8546-38444c0ef708', 'upw_gp_address_postcode', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.postcode', null, 'secure/offenders/crn/$crn/personalContacts', null, false),
('7802580e-fa85-4998-966e-cc29e58f04a5', 'upw_gp_phone_number', 'UPW', 'DELIUS', '$[?(@.relationshipType.code==''RT02'')].address.telephoneNumber', null, 'secure/offenders/crn/$crn/personalContacts', null, false),

('f4a45236-87c9-457b-b9a3-78db3384e870', 'upw_allergies', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''D'' && @.personalCircumstanceSubType.code==''D03'')].personalCircumstanceSubType.description', 'yesno', 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('e0004447-79e0-497c-85ff-303238555ab4', 'upw_allergies_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''D'' && @.personalCircumstanceSubType.code==''D03'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('581060dd-12d5-4f07-bca5-ef1526316dae', 'upw_pregnancy', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''PM'' && @.personalCircumstanceSubType.code==''D06'')].personalCircumstanceSubType.description', 'mapped', 'secure/offenders/crn/$crn/personalCircumstances', 'PREGNANT', false),
('2bfe4909-4fa6-42aa-9c10-05c831368df4', 'upw_pregnancy', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''PM'' && @.personalCircumstanceSubType.code==''D07'')].personalCircumstanceSubType.description', 'mapped', 'secure/offenders/crn/$crn/personalCircumstances', 'RECENTLY_GIVEN_BIRTH', false),
('84c150fc-2466-4f77-8ac7-245fb599fc11', 'upw_pregnancy', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''PM'')]', 'mapped', 'secure/offenders/crn/$crn/personalCircumstances', 'NO', true),
('52bb75ca-4839-4276-b84e-618bbf171f98', 'upw_pregnancy_pregnant_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''PM'' && @.personalCircumstanceSubType.code==''D06'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('7ea11ea2-9d52-421e-8f71-8cf769026545', 'upw_pregnancy_recently_given_birth_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''PM'' && @.personalCircumstanceSubType.code==''D07'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('d48a7841-8398-4964-8fa9-30b932191296', 'upw_caring_commitments', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''I'')].personalCircumstanceSubType.description', 'yesno', 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('e2765bb6-5d2a-47c5-babb-d8cbd9842c26', 'upw_caring_commitments_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''I'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('cdd2f423-7805-4775-9ca2-ea23bf268365', 'upw_reading_writing_difficulties', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''G'')].personalCircumstanceSubType.description', 'yesno', 'secure/offenders/crn/$crn/personalCircumstances', null, false),
('6a65d799-3796-497c-b09d-8dab9822fb6c', 'upw_reading_writing_difficulties_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceType.code==''G'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false)
;