
INSERT INTO assessment (assessment_id, assessment_uuid, supervision_id, created_date, completed_date )
VALUES (99, 'e69a61ff-7395-4a12-b434-b1aa6478aded', '12345', '2019-11-14 08:11:53.177108', null);


INSERT INTO assessed_episode (episode_id, episode_uuid, user_id, created_date, end_date, change_reason, assessment_uuid, answers  )
VALUES (99, '4511a3f6-7f51-4b96-b603-4e75eac0c839', 'employee1', '2019-11-14 08:11:53.177108', null, 'new episode', 'e69a61ff-7395-4a12-b434-b1aa6478aded',
    '{ "11111111-1111-1111-1111-111111111111": {
        "freeTextAnswer": "some free text",
        "answers": {
            "4444444-4444-4444-4444-444444444441": "Some free text",
            "4444444-4444-4444-4444-444444444442": "Some more free text"
        }
    }}');
