-- noinspection SqlResolveForFile

DELETE FROM assessed_episode WHERE true;
DELETE FROM subject WHERE true;
DELETE FROM assessment WHERE true;
DELETE FROM oasys_question_mapping WHERE true;
DELETE FROM answer_schema WHERE true;
DELETE FROM oasys_reference_data_target_mapping WHERE true;
DELETE FROM predictor_field_mapping WHERE true;
DELETE FROM assessment_predictors WHERE true;
DELETE FROM question_schema WHERE true;
DELETE FROM answer_schema_group WHERE true;
DELETE FROM question_group WHERE true;
DELETE FROM assessment_schema_groups WHERE true;
DELETE FROM assessment_schema WHERE true;
DELETE FROM grouping WHERE true;
DELETE FROM question_dependency WHERE true;