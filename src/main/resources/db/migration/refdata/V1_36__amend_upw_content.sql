UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please outline reasons why this would not be suitable.'
WHERE question_code = 'no_female_supervisor';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Please outline reasons why this would not be suitable.'
WHERE question_code = 'no_male_supervisor';
UPDATE hmppsassessmentsschemas.question
SET question_help_text = 'Individual placements are often in charity shops or other smaller organisations meaning the person will not be supervised by a member of probation staff. Give any reasons why this person should not work on a partner supervised placement or individual placement.'
WHERE question_code = 'risk_management_issues_individual';
UPDATE hmppsassessmentsschemas.question
SET question_text = 'Is the individual in employment or education?'
WHERE question_code = 'employment_education';
UPDATE hmppsassessmentsschemas.question
SET question_text = 'If the person on probation is unemployed, are they available to work intensively? (Up to a maximum 28 Hours a week)'
WHERE question_code = 'availability_intensive_working';
UPDATE hmppsassessmentsschemas.question
SET question_text = 'If the person on probation is unemployed, are they available to work Mon-Fri?'
WHERE question_code = 'availability_availability_weekdays';
