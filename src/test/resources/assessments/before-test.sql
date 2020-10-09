-- noinspection SqlResolveForFile

delete from assessed_episode where true;
delete from assessment where true;

/* Assessment with Episodes */
insert into assessment  (assessment_id, assessment_uuid, supervision_id, created_date) values
(1, '2e020e78-a81c-407f-bc78-e5f284e237e5', 'CRN1', '2019-11-14 09:00');

insert into assessed_episode  (episode_id, episode_uuid, user_id, created_date, end_date, change_reason, assessment_uuid) values
(1, 'd7aafe55-0cff-4f20-a57a-b66d79eb9c91', 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5'),
(2, 'f3569440-efd5-4289-8fdd-4560360e5259', 'USER1', '2019-11-14 09:00', null,'More Change of Circs', '2e020e78-a81c-407f-bc78-e5f284e237e5');




/* Empty assessment */
insert into assessment  (assessment_id, assessment_uuid, supervision_id, created_date) values
(2, 'f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8', 'CRN2', '2020-1-14 09:00');
