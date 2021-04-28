package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.EpisodeRepository
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.ValidationErrorDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Complete Tests")
class AssessmentServiceCompleteTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val episodeService: EpisodeService = mockk()
  private val offenderService: OffenderService = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()

  private val assessmentsService = AssessmentService(
    assessmentRepository,
    episodeRepository,
    subjectRepository,
    questionService,
    episodeService,
    courtCaseRestClient,
    assessmentUpdateRestClient,
    offenderService
  )

  @Test
  fun `close episode`() {
    val assessment = assessmentEntity()
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    every {
      assessmentUpdateRestClient.completeAssessment(9999, 7777, AssessmentType.SHORT_FORM_PSR)
    } returns UpdateAssessmentAnswersResponseDto(7777)

    val episode = assessmentsService.closeCurrentEpisode(UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d"))

    verify(exactly = 1) { episodeRepository.save(any()) }
    verify(exactly = 1) { assessmentUpdateRestClient.completeAssessment(any(), any(), any(), any()) }
    assertThat(episode.ended).isEqualToIgnoringMinutes(LocalDateTime.now())
  }

  @Test
  fun `close episode for assessment with no episodes throws exception`() {
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessmentWithNoEpisodeEntity()

    assertThrows<EntityNotFoundException> { assessmentsService.closeCurrentEpisode(UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d")) }
    verify(exactly = 0) { episodeRepository.save(any()) }
    verify(exactly = 0) { assessmentUpdateRestClient.completeAssessment(any(), any(), any(), any()) }
  }

  @Test
  fun `close episode with oasys errors does not close episode`() {
    val assessment = assessmentEntity()
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    every {
      assessmentUpdateRestClient.completeAssessment(9999, 7777, AssessmentType.SHORT_FORM_PSR)
    } returns oasysAssessmentError()

    val episode = assessmentsService.closeCurrentEpisode(UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d"))

    verify(exactly = 0) { episodeRepository.save(any()) }
    verify(exactly = 1) { assessmentUpdateRestClient.completeAssessment(any(), any(), any(), any()) }
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
        assessmentType = AssessmentType.SHORT_FORM_PSR,
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
