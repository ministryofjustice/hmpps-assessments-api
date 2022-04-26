INSERT INTO question_schema (question_schema_uuid, question_code, question_start, question_end, answer_type, answer_schema_group_uuid, question_text, question_help_text, reference_data_category)
VALUES
    ('fc89cee5-5fdc-427f-ac70-f2e9e798507f', 'reading_literacy_concerns', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any difficulties with reading or writing?', '', null),
    ('154a2f2b-e3f0-4950-8c33-b21804967b2d', 'reading_literacy_concerns_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null),
    ('c998584e-d6e4-4fde-a182-6e9eb67bbba0', 'numeracy_concerns', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any difficulties with numbers?', '', null),
    ('d19ff149-565c-4837-9948-e065c566df17', 'numeracy_concerns_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null),
    ('2effeada-9e17-4e44-a080-a7569b3ebfbd', 'language_communication_concerns', '2021-09-27 14:50:00', null, 'radio', '887f4528-06d1-4247-8bc3-5e679222baa6', 'Does the individual have any difficulties with language or communication?', '', null),
    ('5fd90bcf-e5a1-4fe0-b639-40c2e8ddeda1', 'language_communication_concerns_details', '2021-09-27 14:50:00', null, 'textarea', null, 'Give details', null, null);


INSERT INTO external_source_question_mapping (external_source_question_mapping_uuid, question_code, assessment_schema_code, external_source, json_path_field, field_type, external_source_endpoint, mapped_value, if_empty)
VALUES
    ('cba22624-7d64-4a35-9452-492bb17864ac', 'reading_literacy_concerns', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceSubType.code==''G01'')].personalCircumstanceSubType.description', 'yesno', 'secure/offenders/crn/$crn/personalCircumstances', null, false),
    ('3a174304-4d43-4d25-9c6a-79572d7ed95e', 'reading_literacy_concerns_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceSubType.code==''G01'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false),
    ('18116e4c-713f-4f3e-b13f-e56091eea1c1', 'numeracy_concerns', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceSubType.code==''G02'')].personalCircumstanceSubType.description', 'yesno', 'secure/offenders/crn/$crn/personalCircumstances', null, false),
    ('bdedafc1-7300-41c3-bb5a-1f1cd242bc40', 'numeracy_concerns_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceSubType.code==''G02'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false),
    ('e3c4bdfb-a0bf-4c7a-a6cc-0e6e4d6a20c5', 'language_communication_concerns', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceSubType.code==''G03'')].personalCircumstanceSubType.description', 'yesno', 'secure/offenders/crn/$crn/personalCircumstances', null, false),
    ('0cab0a0c-e80e-4c14-bcb6-f0afba2239b5', 'language_communication_concerns_details', 'UPW', 'DELIUS', '$.personalCircumstances[?(@.personalCircumstanceSubType.code==''G03'')].notes', null, 'secure/offenders/crn/$crn/personalCircumstances', null, false)

