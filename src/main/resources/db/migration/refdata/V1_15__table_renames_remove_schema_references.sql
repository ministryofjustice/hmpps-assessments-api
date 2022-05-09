-- Rename answer_schema_group table and associated columns

ALTER TABLE answer_schema_group RENAME TO answer_group;

ALTER TABLE answer_group RENAME COLUMN answer_schema_group_id TO answer_group_id;
ALTER TABLE answer_group RENAME COLUMN answer_schema_group_uuid TO answer_group_uuid;
ALTER TABLE answer_group RENAME COLUMN answer_schema_group_code TO answer_group_code;

-- Rename answer_schema table and associated columns
ALTER TABLE answer_schema RENAME TO answer;

ALTER TABLE answer RENAME COLUMN answer_schema_id TO answer_id;
ALTER TABLE answer RENAME COLUMN answer_schema_uuid TO answer_uuid;
ALTER TABLE answer RENAME COLUMN answer_schema_code TO answer_code;
ALTER TABLE answer RENAME COLUMN answer_schema_group_uuid TO answer_group_uuid;

-- Rename question_schema table and associated columns
ALTER TABLE question_schema RENAME TO question;

ALTER TABLE question RENAME COLUMN question_schema_id TO question_id;
ALTER TABLE question RENAME COLUMN question_schema_uuid TO question_uuid;
ALTER TABLE question RENAME COLUMN answer_schema_group_uuid TO answer_group_uuid;
-- Rename assessment_schema table and associated columns
ALTER TABLE assessment_schema RENAME TO assessment;
ALTER TABLE assessment RENAME COLUMN assessment_schema_id TO assessment_id;
ALTER TABLE assessment RENAME COLUMN assessment_schema_uuid TO assessment_uuid;
ALTER TABLE assessment RENAME COLUMN assessment_schema_code TO assessment_type;

-- Rename assessment_schema_groups table and associated columns
ALTER TABLE assessment_schema_groups RENAME TO assessment_groups;
ALTER TABLE assessment_groups RENAME COLUMN assessment_schema_group_id TO assessment_group_id;
ALTER TABLE assessment_groups RENAME COLUMN assessment_schema_uuid TO assessment_uuid;

-- Rename misc column names
ALTER TABLE assessment_predictors RENAME COLUMN assessment_schema_code TO assessment_type;
ALTER TABLE clone_assessment_excluded_questions RENAME COLUMN assessment_schema_code TO assessment_type;
ALTER TABLE external_source_question_mapping RENAME COLUMN assessment_schema_code TO assessment_type;
ALTER TABLE oasys_question_mapping RENAME COLUMN question_schema_uuid TO question_uuid;
ALTER TABLE oasys_reference_data_target_mapping RENAME COLUMN question_schema_uuid TO question_uuid;
ALTER TABLE oasys_reference_data_target_mapping RENAME COLUMN parent_question_schema_uuid TO parent_question_uuid;
ALTER TABLE predictor_field_mapping RENAME COLUMN question_schema_uuid TO question_uuid;


