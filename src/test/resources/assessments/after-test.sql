-- noinspection SqlResolveForFile

delete from assessed_episode where true;
delete from subject where true;
delete from assessment where true;

delete from question_group where question_group_uuid = 'c093a4ea-46a2-4b98-89cc-6bacaad4d401';
delete from question_schema where question_schema_uuid = '23c3e984-54c7-480f-b06c-7d000e2fb87c';
delete from grouping where group_uuid = 'fb777be0-a183-4c83-8209-e7871df9c547';
