-- Update question text in GP details
UPDATE question SET question_text = 'GP practice name' WHERE question_uuid = '6001121c-5b82-467f-bcfe-597f5e91e2f0'; -- gp_practice_name
UPDATE question SET question_text = 'Name (Optional)' WHERE question_uuid = '4b1e24af-6eb0-4d3c-add2-c6716878d480'; -- gp name field

-- Update question text in Emergency contacts
UPDATE question SET question_text = 'Name' WHERE question_uuid = '70eb1db7-4e68-4a59-b40c-7ce00f9c0aec'; -- emergency_contact_first_name
UPDATE question SET question_text = 'Surname' WHERE question_uuid = 'c5645fe0-8e9b-4402-8392-629d1b7bfba8'; -- emergency_contact_family_name
UPDATE question SET question_text = 'Mobile' WHERE question_uuid = '07334e88-94ab-4121-ad51-1b9ae050a3cd'; -- emergency_contact_mobile_phone_number

