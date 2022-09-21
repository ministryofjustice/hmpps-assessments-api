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
      AnswerDependency("gender_identity", setOf("FEMALE", "NON_BINARY", "PREFER_NOT_TO_SAY"), setOf("placement_preference", "placement_preferences", "placement_preference_complete")),
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
