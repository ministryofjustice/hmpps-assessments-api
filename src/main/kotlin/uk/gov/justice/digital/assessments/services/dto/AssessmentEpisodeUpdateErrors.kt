package uk.gov.justice.digital.assessments.services.dto

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.services.QuestionSchemaEntities
import java.util.UUID

class AssessmentEpisodeUpdateErrors(
  private val answerErrors: MutableMap<UUID, MutableCollection<String>> = mutableMapOf(),
  private val errorsOnPage: MutableList<String> = mutableListOf(),
  private val errorsInAssessment: MutableList<String> = mutableListOf(),
) {
  val errors: Map<UUID, Collection<String>>?
    get() = answerErrors.ifEmpty { null }
  val pageErrors: Collection<String>?
    get() = errorsOnPage.ifEmpty { null }
  val assessmentErrors: Collection<String>?
    get() = errorsInAssessment.ifEmpty { null }

  private fun addAnswerError(question: UUID, message: String?) {
    answerErrors.putIfAbsent(question, mutableListOf())
    answerErrors[question]?.add(message ?: "Field validation error")
  }

  private fun addPageError(message: String?) {
    errorsOnPage.add(message ?: "Error on page")
  }
  private fun addAssessmentError(message: String?) {
    errorsInAssessment.add(message ?: "Error in episode")
  }

  companion object {
    fun mapOasysErrors(
      episode: AssessmentEpisodeEntity,
      questions: QuestionSchemaEntities?,
      oasysUpdateResult: UpdateAssessmentAnswersResponseDto?
    ): AssessmentEpisodeUpdateErrors? {
      if (oasysUpdateResult == null || oasysUpdateResult.validationErrorDtos.isEmpty())
        return null

      val updateErrors = AssessmentEpisodeUpdateErrors()
      if (questions != null) {
        mapAnswerErrors(updateErrors, episode, questions, oasysUpdateResult)
      }
      mapPageErrors(updateErrors, oasysUpdateResult)
      mapAssessmentErrors(updateErrors, oasysUpdateResult)
      return updateErrors
    }

    private fun mapAnswerErrors(
      updateErrors: AssessmentEpisodeUpdateErrors,
      episode: AssessmentEpisodeEntity,
      questions: QuestionSchemaEntities,
      oasysUpdateResult: UpdateAssessmentAnswersResponseDto
    ) {
      val questionsInThisEpisode = episode.answers?.keys ?: emptySet()

      oasysUpdateResult.validationErrorDtos.forEach {
        val mappedQuestions = questions.forOasysMapping(it.sectionCode, it.logicalPage, it.questionCode)
        mappedQuestions
          .filter { q -> questionsInThisEpisode.contains(q.questionSchemaUuid) }
          .forEach { q -> updateErrors.addAnswerError(q.questionSchemaUuid, it.message) }
      }
    } // mapAnswerErrors

    private fun mapPageErrors(
      updateErrors: AssessmentEpisodeUpdateErrors,
      oasysUpdateResult: UpdateAssessmentAnswersResponseDto
    ) {
      oasysUpdateResult.validationErrorDtos
        .filter { it.questionCode == null }
        .forEach { updateErrors.addPageError(it.message) }
    }

    private fun mapAssessmentErrors(
      updateErrors: AssessmentEpisodeUpdateErrors,
      oasysUpdateResult: UpdateAssessmentAnswersResponseDto
    ) {
      oasysUpdateResult.validationErrorDtos
        .filter { it.sectionCode == "ASSESSMENT" }
        .forEach { updateErrors.addAssessmentError(it.message) }
    }
  }

  fun hasErrors(): Boolean {
    return answerErrors.isNotEmpty() || errorsOnPage.isNotEmpty() || errorsInAssessment.isNotEmpty()
  }
}
