-- Update question text from unpaid work
UPDATE question_schema_uuid SET question_text = 'Town or city' WHERE content_uuid = '5202c7de-5fc7-4132-af7b-28a02ac5e330'; -- contact information field
UPDATE question_schema_uuid SET question_text = 'Town or city' WHERE content_uuid = '3d7c78eb-5fe9-42ca-9353-e0e4722f97d9'; -- emergency contact information field
UPDATE question_schema_uuid SET question_text = 'Town or city' WHERE content_uuid = 'f78e069b-3b77-44bf-a698-ae0392e918b5'; -- GP details field
UPDATE question_schema_uuid SET question_help_text = 'This could include their name, appearance, the way they dress, taking hormones, or having gender-confirming surgery.' WHERE content_uuid = '2e9b7f55-2724-456c-b0cb-9d8326b90cf4'; -- transgender field

