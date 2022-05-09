-- noinspection SqlResolveForFile
DELETE FROM hmppsassessmentsapi.assessed_episode WHERE true;
DELETE FROM hmppsassessmentsapi.author WHERE true;
DELETE FROM hmppsassessmentsapi.offence WHERE true;
DELETE FROM hmppsassessmentsapi.assessment WHERE true;
DELETE FROM hmppsassessmentsapi.subject WHERE true;

DELETE FROM hmppsassessmentsschemas.question_group WHERE question_group_uuid IN ('2d267ba0-5ac5-473f-8369-8ecc424ad0c3', '435a2cbc-a7e9-4ba6-808e-dfe6cd60971f', '47b86036-2a9d-4363-a4a1-fc063a41df4a');
DELETE FROM hmppsassessmentsschemas.oasys_question_mapping WHERE mapping_uuid IN ('b28a7159-edbc-409a-8a53-3d6b8a4ae3b6', '1713e728-7738-48d7-8060-3f6f014d6c5c');
DELETE FROM hmppsassessmentsschemas.grouping WHERE group_uuid IN ('1250321c-feff-4b87-83a7-00a65095cab1', '8dc6d75e-7908-4f3b-97d4-48d5441af5e1');
DELETE FROM hmppsassessmentsschemas.question WHERE question_uuid IN ('2fe94330-22c4-4a6e-a494-9f53dc2139c6','8d48ff4d-60f6-461b-ab00-67d1c2ed5f6b');
