package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
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
      if (hasRequiredFieldsForScore(it.predictorFields.toList(), episodeAnswers)) {
        // Request predictor score and return PredictorResult
        1
      } else {
        // Return PredictorResult as N/A
        0
      }
    }
  }

  private fun hasRequiredFieldsForScore(
    predictorFields: List<PredictorFieldMapping>,
    answers: Map<UUID, AnswersDto>
  ): Boolean {
    val numberOfAnsweredPredictorFields = predictorFields.fold(0) {
      total, field -> if (answers[field.questionSchema.questionSchemaUuid] != null) total + 1 else total
    }

    return numberOfAnsweredPredictorFields == predictorFields.size
  }
}
