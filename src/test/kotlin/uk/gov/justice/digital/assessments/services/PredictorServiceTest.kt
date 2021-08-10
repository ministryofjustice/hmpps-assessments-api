package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.PredictorResultStatus.DETERMINED
import uk.gov.justice.digital.assessments.api.PredictorResultStatus.UNDETERMINED
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.Predictor
import uk.gov.justice.digital.assessments.jpa.entities.PredictorFieldMapping
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import java.time.LocalDateTime
import java.util.UUID

class PredictorServiceTest {
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient = mockk()

  private val predictorService = PredictorService(assessmentSchemaService, assessRisksAndNeedsApiRestClient)

  private val testQuestion = QuestionSchemaEntity(
    questionSchemaId = 1,
    questionSchemaUuid = UUID.randomUUID(),
  )

  private val predictors = listOf(
    Predictor(
      1,
      AssessmentSchemaCode.RSR,
      PredictorType.RSR,
      listOf(
        PredictorFieldMapping(
          1,
          UUID.randomUUID(),
          testQuestion,
          PredictorType.RSR,
          "predictor_field_name"
        )
      )
    )
  )

  private val answers = mutableMapOf(
    testQuestion.questionSchemaUuid to AnswerEntity(
      listOf(Answer(items = listOf("TEST_VALUE")))
    )
  )

  private val assessmentEpisode = AssessmentEpisodeEntity(
    episodeId = 1,
    episodeUuid = UUID.randomUUID(),
    answers = answers,
    createdDate = LocalDateTime.now(),
  )

  private val assessmentEpisodeNoAnswers = AssessmentEpisodeEntity(
    episodeId = 2,
    episodeUuid = UUID.randomUUID(),
    createdDate = LocalDateTime.now(),
  )

  @BeforeEach
  private fun setup() {
    every { assessRisksAndNeedsApiRestClient.getRiskPredictors(any(), any()) } returns null
  }

  @Nested
  @DisplayName("get predictor results")
  inner class GetPredictorResults {
    @Test
    fun `returns predictor scores for the assessment code`() {
      every { assessmentSchemaService.getPredictorsForAssessment(AssessmentSchemaCode.RSR) } returns predictors

      val results = predictorService.getPredictorResults(AssessmentSchemaCode.RSR, assessmentEpisode)

      assertThat(results).hasSize(1)
      assertThat(results.first().type).isEqualTo(PredictorType.RSR)
      assertThat(results.first().status).isEqualTo(DETERMINED)
      assertThat(results.first().score).isEqualTo(1234)
    }

    @Test
    fun `does not return a score when predictor conditions are not met`() {
      every { assessmentSchemaService.getPredictorsForAssessment(AssessmentSchemaCode.RSR) } returns predictors

      val results = predictorService.getPredictorResults(AssessmentSchemaCode.RSR, assessmentEpisodeNoAnswers)

      assertThat(results).hasSize(1)
      assertThat(results.first().type).isEqualTo(PredictorType.RSR)
      assertThat(results.first().status).isEqualTo(UNDETERMINED)
      assertThat(results.first().score).isNull()
    }
  }
}
