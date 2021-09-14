-- noinspection SqlResolveForFile

DELETE FROM hmppsassessmentsschemas.oasys_question_mapping WHERE mapping_uuid IN ('204b461b-90af-4e11-b57f-7ccb07b67059', '5bfbc30d-811f-443e-8f82-8d86eaadbbe4');
DELETE FROM hmppsassessmentsschemas.answer_schema WHERE answer_schema_uuid IN ('464e25da-f843-43b6-8223-4af415abda0c',
    '0a428566-6393-462f-addb-50feaaf75d57');
DELETE FROM hmppsassessmentsschemas.question_dependency WHERE subject_question_uuid IN ('11111111-1111-1111-1111-111111111113',
                                                                                        '11111111-1111-1111-1111-111111111116',
                                                                                        '11111111-1111-1111-1111-111111111117',
                                                                                        '11111111-1111-1111-1111-111111111118',
                                                                                        '11111111-1111-1111-1111-111111111119',
                                                                                        '1948af63-07f2-4a8c-9e4c-0ec347bd6ba8');
DELETE FROM hmppsassessmentsschemas.question_schema WHERE question_schema_uuid IN (
                                                                                   'fd412ca8-d361-47ab-a189-7acb8ae0675b',
                                                                                   '1948af63-07f2-4a8c-9e4c-0ec347bd6ba8',
                                                                                   'a5830801-533c-4b9e-bab1-03272718d6dc',
                                                                                   'a8e303f5-5f88-4343-94d1-a369ca1f86cb',
                                                                                   'b9dd3680-c4d6-403e-8f27-8d65481cbf44',
                                                                                   '11111111-1111-1111-1111-111111111112',
                                                                                   '11111111-1111-1111-1111-111111111113',
                                                                                   '11111111-1111-1111-1111-111111111115',
                                                                                   '11111111-1111-1111-1111-111111111116',
                                                                                   '11111111-1111-1111-1111-111111111117',
                                                                                   '11111111-1111-1111-1111-111111111118',
                                                                                   '11111111-1111-1111-1111-111111111119');
DELETE FROM hmppsassessmentsschemas.answer_schema_group WHERE answer_schema_group_uuid = 'f756f79d-dfad-49f9-a1b9-964a41cf660d';


DELETE FROM hmppsassessmentsschemas.question_group WHERE question_group_uuid IN ('334f3e21-b249-4c7f-848e-05c0d2aad8f4',
                                                                                'fcec5c32-ea96-424c-80a5-8186dc414619',
                                                                                'c1d9281d-2363-43a7-9e02-bd19c13d685f',
                                                                                '67b942c8-86f6-4493-af53-9f814b41f344',
                                                                                '6c0c874f-cd71-4422-b153-2cb270183b5c');
DELETE FROM hmppsassessmentsschemas.assessment_schema_groups WHERE assessment_schema_uuid IN ('51c2e87e-a540-4027-8f5a-e6c80511332f', 'c3a6beac-37c0-46b6-b4b3-62086b624675');
DELETE FROM hmppsassessmentsschemas.grouping WHERE group_uuid IN ('e964d699-cf96-4abd-af0e-ddf1f6687a46',
    'e353f3df-113d-401c-a3c0-14239fc17cf9',
    '6afbe596-9956-4620-824b-c6c9000ace7c');
