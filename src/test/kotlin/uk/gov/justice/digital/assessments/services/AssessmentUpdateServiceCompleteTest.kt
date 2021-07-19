package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.ValidationErrorDto
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Complete Tests")
class AssessmentUpdateServiceCompleteTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val assessmentSchemaService: AssessmentSchemaService = mockk()
  private val predictorService: PredictorService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    assessmentUpdateRestClient,
    predictorService,
    assessmentSchemaService
  )

  @BeforeEach
  fun setup() {
    every { assessmentSchemaService.toOasysAssessmentType(AssessmentSchemaCode.ROSH) } returns OasysAssessmentType.SHORT_FORM_PSR
  }

  @Test
  fun `close episode`() {
    val assessment = assessmentEntity()
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    every {
      assessmentUpdateRestClient.completeAssessment(9999, OasysAssessmentType.SHORT_FORM_PSR, 7777)
    } returns UpdateAssessmentAnswersResponseDto(7777)

    val episode = assessmentUpdateService.closeEpisode(assessment.episodes.first())

    verify(exactly = 1) { episodeRepository.save(any()) }
    verify(exactly = 1) {
      assessmentUpdateRestClient.completeAssessment(9999, OasysAssessmentType.SHORT_FORM_PSR, 7777)
    }
    assertThat(episode.ended).isEqualToIgnoringMinutes(LocalDateTime.now())
  }

  @Test
  fun `close episode with oasys errors does not close episode`() {
    val assessment = assessmentEntity()
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    every {
      assessmentUpdateRestClient.completeAssessment(9999, OasysAssessmentType.SHORT_FORM_PSR, 7777)
    } returns oasysAssessmentError()

    val episode = assessmentUpdateService.closeEpisode(assessment.episodes.first())

    verify(exactly = 0) { episodeRepository.save(any()) }
    verify(exactly = 1) { assessmentUpdateRestClient.completeAssessment(9999, OasysAssessmentType.SHORT_FORM_PSR, 7777)
    }
    assertThat(episode.ended).isNull()
  }

  private fun assessmentEntity(): AssessmentEntity {
    val subject = SubjectEntity(oasysOffenderPk = 9999)
    val episodes = mutableListOf<AssessmentEpisodeEntity>()
    val assessment = AssessmentEntity(
      assessmentUuid = UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d"),
      assessmentId = 1,
      episodes = episodes,
      subject_ = mutableListOf(subject)
    )
    episodes.add(
      AssessmentEpisodeEntity(
        episodeUuid = UUID.fromString("669cdd10-1061-42ec-90d4-e34baab19566"),
        episodeId = 1234,
        assessment = assessment,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        changeReason = "Change of Circs 2",
        oasysSetPk = 7777,
      )
    )
    return assessment
  }

  private fun assessmentWithNoEpisodeEntity(): AssessmentEntity {
    val subject = SubjectEntity(oasysOffenderPk = 9999)
    val episodes = mutableListOf<AssessmentEpisodeEntity>()
    return AssessmentEntity(
      assessmentUuid = UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d"),
      assessmentId = 1,
      episodes = episodes,
      subject_ = mutableListOf(subject)
    )
  }

  private fun oasysAssessmentError(): UpdateAssessmentAnswersResponseDto {
    return UpdateAssessmentAnswersResponseDto(
      7777,
      setOf(
        ValidationErrorDto("ASSESSMENT", null, "Q1", "OOPS", "NO", false)
      )
    )
  }
}
