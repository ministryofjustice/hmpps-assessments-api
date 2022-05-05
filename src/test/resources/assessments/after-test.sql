-- noinspection SqlResolveForFile

DELETE FROM hmppsassessmentsapi.assessed_episode WHERE true;
DELETE FROM hmppsassessmentsapi.author WHERE true;
DELETE FROM hmppsassessmentsapi.offence WHERE true;
DELETE FROM hmppsassessmentsapi.assessment WHERE true;
DELETE FROM hmppsassessmentsapi.subject WHERE true;

DELETE FROM hmppsassessmentsschemas.question WHERE question_uuid = '23c3e984-54c7-480f-b06c-7d000e2fb87c';
DELETE FROM hmppsassessmentsschemas.question_group WHERE question_group_uuid = 'c093a4ea-46a2-4b98-89cc-6bacaad4d401';
DELETE FROM hmppsassessmentsschemas.grouping WHERE group_uuid = 'fb777be0-a183-4c83-8209-e7871df9c547';