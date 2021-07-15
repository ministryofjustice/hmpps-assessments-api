package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import java.util.UUID

@Service
class PredictorService(
  private val assessmentSchemaService: AssessmentSchemaService,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getPredictorResults(
    assessmentSchemaCode: AssessmentSchemaCode,
    episodeAnswers: Map<UUID, AnswersDto>,
  ): List<Int> {
    val predictors = assessmentSchemaService.getPredictorsForAssessment(assessmentSchemaCode)
    log.info("Found ${predictors.size} predictors for assessment type $assessmentSchemaCode")

    return predictors.map {
      val predictorFields = it.predictorFields.toList()
      val extractedAnswers = extractAnswers(it.predictorFields.toList(), episodeAnswers)
      if (predictorFields.isNotEmpty() && predictorFields.size == extractedAnswers.size) {
        // Request predictor score and return PredictorResult
        1
      } else {
        // Return PredictorResult as N/A
        0
      }
    }
  }

  private fun extractAnswers(
    predictorFields: List<PredictorFieldMapping>,
    answers: Map<UUID, AnswersDto>
  ): Map<UUID, Collection<AnswerDto>> {
    return predictorFields
      .map { it.questionSchema.questionSchemaUuid }
      .associate { questionSchemaUuid ->
        val questionAnswer = answers[questionSchemaUuid]
        (questionSchemaUuid to questionAnswer?.answers.orEmpty())
      }
      .filterValues { it.isNotEmpty() }
  }
}
