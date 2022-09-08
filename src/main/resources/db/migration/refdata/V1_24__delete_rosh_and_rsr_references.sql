DELETE FROM external_source_question_mapping WHERE assessment_type IN ('RSR', 'ROSH');

drop table predictor_field_mapping;
drop table assessment_predictors;

drop table oasys_question_mapping;
drop table oasys_reference_data_target_mapping;