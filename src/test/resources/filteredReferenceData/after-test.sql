-- noinspection SqlResolveForFile
DELETE FROM hmppsassessmentsapi.assessed_episode WHERE true;
DELETE FROM hmppsassessmentsapi.subject WHERE true;
DELETE FROM hmppsassessmentsapi.assessment WHERE true;

DELETE FROM hmppsassessmentsschemas.question_group WHERE true;
DELETE FROM hmppsassessmentsschemas.oasys_question_mapping WHERE true;
DELETE FROM hmppsassessmentsschemas.oasys_reference_data_target_mapping WHERE true;
DELETE FROM hmppsassessmentsschemas.assessment_schema_groups WHERE true;
DELETE FROM hmppsassessmentsschemas.assessment_schema WHERE true;
DELETE FROM hmppsassessmentsschemas.grouping WHERE true;
DELETE FROM hmppsassessmentsschemas.predictor_field_mapping WHERE true;
DELETE FROM hmppsassessmentsschemas.assessment_predictors WHERE true;
DELETE FROM hmppsassessmentsschemas.question_dependency WHERE true;
DELETE FROM hmppsassessmentsschemas.question_schema WHERE true;