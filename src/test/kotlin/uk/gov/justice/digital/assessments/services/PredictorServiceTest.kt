package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode.RSR_ONLY
import uk.gov.justice.digital.assessments.jpa.entities.Predictor
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType.RSR
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.util.*

class PredictorServiceTest {
  private val assessmentSchemaService: AssessmentSchemaService = mockk()

  private val predictorService = PredictorService(assessmentSchemaService)

  private val testQuestion = QuestionSchemaEntity(
    questionSchemaId = 1,
    questionSchemaUuid = UUID.randomUUID(),
  )

  private val predictors = listOf(Predictor(
    1,
    RSR_ONLY,
    RSR,
    listOf(
      PredictorFieldMapping(
        1,
        UUID.randomUUID(),
        testQuestion,
        RSR,
        "predictor_field_name"
      )
    )
  ))

  private val answers = mapOf(
    testQuestion.questionSchemaUuid to AnswersDto(
      listOf(AnswerDto(items = listOf("TEST_VALUE")))
    )
  )

  @Nested
  @DisplayName("get predictor results")
  inner class GetPredictorResults {
    @Test
    fun `returns predictor scores for the assessment code`() {
      every { assessmentSchemaService.getPredictorsForAssessment(RSR_ONLY) } returns predictors

      val results = predictorService.getPredictorResults(RSR_ONLY, answers)

      assertThat(results).hasSize(1)
      assertThat(results.first().predictor).isEqualTo(RSR)
      assertThat(results.first().score).isEqualTo(1234)
    }

    @Test
    fun `does not return a score when predictor conditions are not met`() {
      every { assessmentSchemaService.getPredictorsForAssessment(RSR_ONLY) } returns predictors

      val results = predictorService.getPredictorResults(RSR_ONLY, emptyMap())

      assertThat(results).hasSize(1)
      assertThat(results.first().predictor).isEqualTo(RSR)
      assertThat(results.first().score).isNull()
    }
  }
}
