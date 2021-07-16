package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.PredictorScoreDto
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
  ): List<PredictorScoreDto> {
    val predictors = assessmentSchemaService.getPredictorsForAssessment(assessmentSchemaCode)

    log.info("Found ${predictors.size} predictors for assessment type $assessmentSchemaCode")

    return predictors.map { predictor ->
      val predictorFields = predictor.fields.toList()
      val extractedAnswers = extractAnswers(predictorFields, episodeAnswers)
      if (predictorFields.isNotEmpty() && predictorFields.size == extractedAnswers.size)
        PredictorScoreDto(predictor.type, 1234) else PredictorScoreDto.conditionsNotMet(predictor.type)
    }
  }

  private fun extractAnswers(
    predictorFields: List<PredictorFieldMapping>,
    answers: Map<UUID, AnswersDto>
  ): Map<String, Collection<AnswerDto>> {
    return predictorFields
      .associate { predictorField ->
        val questionUuid = predictorField.questionSchema.questionSchemaUuid
        val questionAnswer = answers[questionUuid]
        (predictorField.predictorFieldName to questionAnswer?.answers.orEmpty())
      }
      .filterValues { extractedAnswer -> extractedAnswer.isNotEmpty() }
  }
}
