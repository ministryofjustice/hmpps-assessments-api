-- Update section complete question content
UPDATE question SET question_text = 'Mark individual’s details section as complete?' WHERE question_code = 'individual_details_complete';
UPDATE question SET question_text = 'Mark gender information section as complete?' WHERE question_code = 'placement_preference_by_gender_complete';
UPDATE question SET question_text = 'Mark cultural or religious adjustments section as complete?' WHERE question_code = 'cultural_religious_adjustment_complete';
UPDATE question SET question_text = 'Mark placement preferences as complete?' WHERE question_code = 'placement_preference_complete';
UPDATE question SET question_text = 'Mark risk of harm in the community section as complete?' WHERE question_code = 'rosh_community_complete';
UPDATE question SET question_text = 'Mark managing risk section as complete?' WHERE question_code = 'managing_risk_complete';
UPDATE question SET question_text = 'Mark disabilities and mental health section as complete?' WHERE question_code = 'disabilities_complete';
UPDATE question SET question_text = 'Mark health issues section as complete?' WHERE question_code = 'health_issues_complete';
UPDATE question SET question_text = 'Mark GP details section as complete?' WHERE question_code = 'gp_details_complete';
UPDATE question SET question_text = 'Mark travel information section as complete?' WHERE question_code = 'travel_information_complete';
UPDATE question SET question_text = 'Mark caring commitments section as complete?' WHERE question_code = 'caring_commitments_complete';
UPDATE question SET question_text = 'Mark employment, education and skills section as complete?' WHERE question_code = 'employment_education_skills_complete';
UPDATE question SET question_text = 'Mark training and employment section as complete?' WHERE question_code = 'employment_training_complete';
UPDATE question SET question_text = 'Mark intensive working section as complete?' WHERE question_code = 'eligibility_intensive_working_complete';
UPDATE question SET question_text = 'Mark availability for community payback work section as complete?' WHERE question_code = 'individual_availability_complete';
UPDATE question SET question_text = 'Mark equipment sizes section as complete?' WHERE question_code = 'equipment_complete';

-- Update legends
UPDATE question SET question_text = 'Does the individual have any known allergies?' WHERE question_code = 'allergies';
UPDATE question SET question_text = 'Has the individual experienced sudden loss of consciousness?' WHERE question_code = 'loss_consciousness';
UPDATE question SET question_text = 'Does the individual have epilepsy?' WHERE question_code = 'epilepsy';
UPDATE question SET question_text = 'Is the individual pregnant or recently given birth?' WHERE question_code = 'pregnancy';
