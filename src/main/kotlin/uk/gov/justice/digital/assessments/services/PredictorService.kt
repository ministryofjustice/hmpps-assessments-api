package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.PredictorScoreDto
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType
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
    episode: AssessmentEpisodeEntity,
  ): List<PredictorScoreDto> {
    val predictors = assessmentSchemaService.getPredictorsForAssessment(assessmentSchemaCode)

    log.info("Found ${predictors.size} predictors for episode ${episode.episodeUuid} with assessment type $assessmentSchemaCode")

    return predictors.map { predictor ->
      val predictorFields = predictor.fields.toList()
      val extractedAnswers = extractAnswers(predictorFields, episode.answers.orEmpty())
      if (predictorFields.isNotEmpty() && predictorFields.size == extractedAnswers.size)
        fetchResults(predictor.type, extractedAnswers) else PredictorScoreDto.incomplete(predictor.type)
    }
  }

  private fun extractAnswers(
    predictorFields: List<PredictorFieldMapping>,
    answers: Map<UUID, AnswerEntity>
  ): Map<String, AnswersDto> {
    return predictorFields
      .associate { predictorField ->
        val questionUuid = predictorField.questionSchema.questionSchemaUuid
        val questionAnswer = answers[questionUuid]
        predictorField.predictorFieldName to questionAnswer?.answers
      }
      .filterValues { it != null && it.isNotEmpty() }
      .mapValues {
        AnswersDto.from(it.value as Collection<Answer>)
      }
  }

  private fun fetchResults(
    predictorType: PredictorType,
    answers: Map<String, AnswersDto>,
  ): PredictorScoreDto {
    log.info("Stubbed call to get Predictor Score")
    return PredictorScoreDto(
      type = predictorType,
      score = 1234
    )
  }
}
