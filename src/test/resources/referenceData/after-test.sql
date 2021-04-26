-- noinspection SqlResolveForFile

DELETE FROM answer_schema WHERE true;

DELETE FROM oasys_question_mapping WHERE true;
DELETE FROM oasys_reference_data_target_mapping WHERE true;

DELETE FROM question_group WHERE true;
DELETE FROM QUESTION_SCHEMA WHERE true;
DELETE FROM grouping WHERE true;

delete from question_dependency where true;
