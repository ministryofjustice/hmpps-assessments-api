ALTER TABLE question_group DROP COLUMN mandatory;
ALTER TABLE question_group DROP COLUMN validation;

-- Begin remove questions with answer_type starts with "presentation:". These aren't question just UI formatting instructions.
DELETE FROM question_group WHERE content_uuid IN (SELECT question_schema_uuid FROM question_schema WHERE answer_type LIKE 'presentation:%');
DELETE FROM question_schema WHERE answer_type LIKE 'presentation:%';
DELETE FROM grouping WHERE group_uuid = '5606da47-8f27-49a0-a943-0f2696f66186'; -- empty group
DELETE FROM question_group WHERE content_uuid = '5606da47-8f27-49a0-a943-0f2696f66186'; -- empty group
-- End remove questions with answer_type starts with "presentation:". These aren't question just UI formatting instructions.