delete from assessed_episode where true;
delete from assessment where true;

/* Assessment with Episodes */
insert into assessment  (assessment_id, supervision_id, created_date) values
(1, 'CRN1', '2019-11-14 09:00');

insert into assessed_episode  (episode_id, user_id, created_date,end_date,change_reason,assessment_id) values
(1, 'USER1', '2019-11-14 09:00', '2019-11-14 12:00','Change of Circs', 1),
(2, 'USER1', '2019-11-14 09:00', null,'More Change of Circs', 1);

/* Empty assessment */
insert into assessment  (assessment_id, supervision_id, created_date) values
(2, 'CRN2', '2020-1-14 09:00');
