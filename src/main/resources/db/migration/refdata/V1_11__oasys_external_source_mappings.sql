INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty, structured_question_code)
VALUES
    ('6b88833e-a698-4067-938e-b63e7aeb24bb', 'date_first_sanction', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.8.2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('688619eb-9c85-44d0-8747-11213667a76a', 'age_first_conviction', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.8'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('6dd6bc54-a73d-47f2-8494-02b91821e788', 'total_sanctions', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.32'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('6046f8a2-eeb1-4aa9-959f-76f298f5a371', 'date_current_conviction', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.29'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('cd1bf717-bdcf-4c2d-b4ee-d3f0b506d470', 'any_sexual_offences', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.30'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('e8b5f035-36a1-484e-b24a-50f54d4b6f64', 'current_offence_victim_stranger', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.42'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('1e54447f-571f-4b07-b261-cad2cbaec0b4', 'current_sexual_offence', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.41'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('c2217e68-a398-48c7-aea2-ddc70d8a6d6a', 'most_recent_sexual_offence_date', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.33'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('b4eee324-b3ba-4cf7-91c3-8aa53d44d87d', 'total_sexual_offences_adult', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.34'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('d59665c1-561b-41ca-b491-1886490b7213', 'total_sexual_offences_child', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.35'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('206990f6-f303-4e16-a73e-18fb1a897e6a', 'total_sexual_offences_child_image', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.36'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('868ec68a-b3da-4791-994d-65c50b3edce7', 'total_non_contact_sexual_offences', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.37'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('e87e4eda-3517-4022-b50e-45f7aaf3fbc1', 'earliest_release_date', 'RSR', 'OASYS', '$.sections[?(@.section==''1'')].answers[?(@.question==''1.38'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('00df1b53-ce04-4f22-a6a9-eb1b72c9fb3c', 'suitable_accommodation', 'RSR', 'OASYS', '$.sections[?(@.section==''3'')].answers[?(@.question==''3.4'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('440d584e-c853-4901-8b98-7fcff1aaef39', 'unemployed_on_release', 'RSR', 'OASYS', '$.sections[?(@.section==''4'')].answers[?(@.question==''4.2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('4ac040fd-94e7-47ee-a1b2-99b2b372e1fc', 'current_relationship_with_partner', 'RSR', 'OASYS', '$.sections[?(@.section==''6'')].answers[?(@.question==''6.4'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('e723e572-3418-4bdd-901f-14e116875de7', 'evidence_domestic_violence', 'RSR', 'OASYS', '$.sections[?(@.section==''6'')].answers[?(@.question==''6.7da'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('edd0923f-b000-40f2-8b13-abb2f894d39a', 'use_of_alcohol', 'RSR', 'OASYS', '$.sections[?(@.section==''9'')].answers[?(@.question==''9.1'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('e912e06a-862a-434c-99a3-311ecc12b86f', 'binge_drinking', 'RSR', 'OASYS', '$.sections[?(@.section==''9'')].answers[?(@.question==''9.2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('8bb15960-95de-4980-a304-ec70132304ac', 'impulsivity_issues', 'RSR', 'OASYS', '$.sections[?(@.section==''11'')].answers[?(@.question==''11.2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('70c06099-1f73-4c1a-b0e8-0d3daaa470d4', 'temper_control_issues', 'RSR', 'OASYS', '$.sections[?(@.section==''11'')].answers[?(@.question==''11.4'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('c63cf305-75d8-47a0-87c8-16632e617551', 'pro_criminal_attitudes', 'RSR', 'OASYS', '$.sections[?(@.section==''12'')].answers[?(@.question==''12.1'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('e018a4a6-8dab-457f-94f8-7ae60b11c3f1', 'current_possession_firearm', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.10.1_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('af2a7061-0a0f-47ff-9f39-1594ee12b25d', 'current_offence_weapon', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.13.1_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('67b577b4-82de-4577-85e6-4ade8e414229', 'previous_murder_attempt', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.1.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('3dfe6a66-d15e-4313-8f12-87cc16c9589c', 'previous_wounding', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.2.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('fd63138b-85cb-42b7-9ed8-99f1087cf886', 'previous_aggravated_burglary', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.6.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('5d429e85-8e8e-4c88-8b59-2735261b5675', 'previous_arson', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.7.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('9e6ab91e-00b4-480a-a6aa-3a2a0eef10bf', 'previous_criminal_damage', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.8.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('bd3a65ec-cc82-488a-bb90-1b476e491ff4', 'previous_kidnapping', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.9.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('c567db7c-7a9e-437f-9d18-4391f4c0751d', 'previous_possession_firearm', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.10.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('de622310-4c08-4b93-b164-791b23edd82b', 'previous_robbery', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.12.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null),
    ('882c37ec-3c2d-4bc8-a42a-13a27d852e50', 'previous_offence_weapon', 'RSR', 'OASYS', '$.sections[?(@.section==''ROSH'')].answers[?(@.question==''R1.2.13.2_V2'')].answer[0]', 'array','assessment/latest/$crn', null, false, null)



