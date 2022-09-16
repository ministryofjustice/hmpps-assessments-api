package uk.gov.justice.digital.assessments.utils

import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity

private class AnswerDependency(
  val triggerQuestionCode: String,
  val triggerAnswerValues: Set<String>,
  val dependentQuestions: Set<String>,
)

class AssessmentUtils {
  companion object {
    private val answerDependencies = listOf(
      AnswerDependency("gender_identity", setOf("FEMALE", "NON_BINARY", "PREFER_TO_SELF_DESCRIBE", "PREFER_NOT_TO_SAY"), setOf("placement_preference", "placement_preferences", "placement_preference_complete")),
      AnswerDependency("sex_change", setOf("YES"), setOf("sex_change_details")),
      AnswerDependency("history_sexual_offending", setOf("YES"), setOf("history_sexual_offending_details")),
      AnswerDependency("poses_risk_to_children", setOf("YES"), setOf("poses_risk_to_children_details")),
      AnswerDependency("violent_offences", setOf("YES"), setOf("violent_offences_details")),
      AnswerDependency("acquisitive_offending", setOf("YES"), setOf("acquisitive_offending_details")),
      AnswerDependency("sgo_identifier", setOf("YES"), setOf("sgo_identifier_details")),
      AnswerDependency("control_issues", setOf("YES"), setOf("control_issues_details")),
      AnswerDependency("history_of_hate_based_behaviour", setOf("YES"), setOf("history_of_hate_based_behaviour_details")),
      AnswerDependency("high_profile_person", setOf("YES"), setOf("high_profile_person_details")),
      AnswerDependency("additional_rosh_info", setOf("YES"), setOf("additional_rosh_info_details")),
      AnswerDependency("location_exclusion_criteria", setOf("YES"), setOf("location_exclusion_criteria_details")),
      AnswerDependency("restricted_placement", setOf("YES"), setOf("restricted_placement_details")),
      AnswerDependency("no_female_supervisor", setOf("YES"), setOf("no_female_supervisor_details")),
      AnswerDependency("no_male_supervisor", setOf("YES"), setOf("no_male_supervisor_details")),
      AnswerDependency("restrictive_orders", setOf("YES"), setOf("restrictive_orders_details")),
      AnswerDependency("risk_management_issues_individual", setOf("YES"), setOf("risk_management_issues_individual_details")),
      AnswerDependency("risk_management_issues_supervised_group", setOf("YES"), setOf("risk_management_issues_supervised_group_details")),
      AnswerDependency("alcohol_drug_issues", setOf("YES"), setOf("alcohol_drug_issues_details")),
      AnswerDependency("physical_disability", setOf("YES"), setOf("physical_disability_details")),
      AnswerDependency("learning_disability", setOf("YES"), setOf("learning_disability_details")),
      AnswerDependency("learning_difficulty", setOf("YES"), setOf("learning_difficulty_details")),
      AnswerDependency("mental_health_condition", setOf("YES"), setOf("mental_health_condition_details")),
      AnswerDependency("additional_disabilities", setOf("YES"), setOf("additional_disabilities_details")),
      AnswerDependency("disabilities", setOf("YES"), setOf("disabilities_details")),
      AnswerDependency("allergies", setOf("YES"), setOf("allergies_details")),
      AnswerDependency("loss_consciousness", setOf("YES"), setOf("loss_consciousness_details")),
      AnswerDependency("epilepsy", setOf("YES"), setOf("epilepsy_details")),
      AnswerDependency("pregnancy", setOf("PREGNANT"), setOf("pregnancy_pregnant_details")),
      AnswerDependency("pregnancy", setOf("RECENTLY_GIVEN_BIRTH"), setOf("pregnancy_recently_given_birth_details")),
      AnswerDependency("other_health_issues", setOf("YES"), setOf("other_health_issues_details")),
      AnswerDependency("travel_information", setOf("YES"), setOf("travel_information_details", "driving_licence", "vehicle", "public_transport")),
      AnswerDependency("caring_commitments", setOf("YES"), setOf("caring_commitments_details")),
      AnswerDependency("employment_education", setOf("FULLTIME_EDUCATION_EMPLOYMENT"), setOf("employment_education_details_fulltime")),
      AnswerDependency("employment_education", setOf("PARTTIME_EDUCATION_EMPLOYMENT"), setOf("employment_education_details_parttime")),
      AnswerDependency("reading_writing_difficulties", setOf("YES"), setOf("reading_writing_difficulties_details")),
      AnswerDependency("work_skills", setOf("YES"), setOf("work_skills_details")),
      AnswerDependency("future_work_plans", setOf("YES"), setOf("future_work_plans_details")),
      AnswerDependency("education_training_need", setOf("YES"), setOf("education_training_need_details", "individual_commitment")),
      AnswerDependency("individual_commitment", setOf("YES"), setOf("individual_commitment_details")),
      AnswerDependency("eligibility_intensive_working", setOf("NO"), setOf("eligibility_intensive_working_details")),
      AnswerDependency("eligibility_intensive_working", setOf("YES"), setOf("recommended_hours_start_order", "recommended_hours_midpoint_order", "twenty_eight_hours_working_week_details")),
      AnswerDependency("active_carer_commitments", emptySet(), setOf("active_carer_commitments_details")),
    )

    fun removeOrphanedAnswers(episode: AssessmentEpisodeEntity) {
      this.answerDependencies.forEach {
        val currentAnswers = episode.answers[it.triggerQuestionCode].orEmpty()

        if (
          isNotAnswered(currentAnswers) ||
          doesNotMatchTriggeringValues(it.triggerAnswerValues, currentAnswers) ||
          hasNoTriggerValuesButNotAnswered(it.triggerAnswerValues, currentAnswers)
        ) {
          it.dependentQuestions.forEach { questionCode -> episode.answers.remove(questionCode) }
        }
      }
    }

    private fun isNotAnswered(currentAnswers: List<Any>): Boolean = currentAnswers.isEmpty()

    private fun doesNotMatchTriggeringValues(
      triggerAnswerValues: Set<String>,
      currentAnswers: List<Any>
    ): Boolean = triggerAnswerValues.isNotEmpty() && currentAnswers.intersect(triggerAnswerValues).isEmpty()

    private fun hasNoTriggerValuesButNotAnswered(
      triggerAnswerValues: Set<String>,
      currentAnswers: List<Any>
    ): Boolean = triggerAnswerValues.isEmpty() && isNotAnswered(currentAnswers)
  }
}
