package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.services.exceptions.CannotCloseEpisodeException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Complete Tests")
class AssessmentUpdateServiceCloseTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val questionService: QuestionService = mockk()
  private val riskPredictorsService: RiskPredictorsService = mockk()
  private val oasysAssessmentUpdateService: OasysAssessmentUpdateService = mockk()
  private val assessmentService: AssessmentService = mockk()
  private val authorService: AuthorService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    questionService,
    riskPredictorsService,
    oasysAssessmentUpdateService,
    assessmentService,
    authorService,
    auditService,
    telemetryService
  )

  @Test
  fun `closes an episode`() {
    val assessment = assessmentEntity()
    justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
    justRun {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_COMPLETE,
        any(),
        any(),
        any(),
        any(),
        any()
      )
    }
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    val assessmentEpisode = assessment.episodes.first()
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    val episode = assessmentUpdateService.closeEpisode(assessmentEpisode)

    verify(exactly = 1) { episodeRepository.save(any()) }
    assertThat(episode.closedDate).isEqualToIgnoringMinutes(LocalDateTime.now())
  }

  @Test
  fun `throws when trying to close a completed episode`() {
    val assessment = assessmentEntity()
    val episode = assessment.episodes[0]
    episode.endDate = LocalDateTime.now()

    justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
    justRun {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_COMPLETE,
        any(),
        any(),
        any(),
        any(),
        any()
      )
    }
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    val assessmentEpisode = assessment.episodes.first()
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    assertThrows<CannotCloseEpisodeException> { assessmentUpdateService.closeEpisode(assessmentEpisode) }
  }

  private fun assessmentEntity(): AssessmentEntity {
    val subject = SubjectEntity(
      oasysOffenderPk = 9999,
      dateOfBirth = LocalDate.of(1989, 1, 1),
      crn = "X1345"
    )
    val episodes = mutableListOf<AssessmentEpisodeEntity>()
    val assessment = AssessmentEntity(
      assessmentUuid = UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d"),
      assessmentId = 1,
      episodes = episodes,
      subject = subject
    )
    episodes.add(
      AssessmentEpisodeEntity(
        episodeUuid = UUID.fromString("669cdd10-1061-42ec-90d4-e34baab19566"),
        episodeId = 1234,
        assessment = assessment,
        assessmentSchemaCode = AssessmentSchemaCode.ROSH,
        changeReason = "Change of Circs 2",
        oasysSetPk = 7777,
        createdDate = LocalDateTime.now(),
        author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      )
    )
    return assessment
  }
}
