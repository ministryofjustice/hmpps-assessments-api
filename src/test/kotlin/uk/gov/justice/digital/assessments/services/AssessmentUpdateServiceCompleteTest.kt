package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.AuditType
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Complete Tests")
class AssessmentUpdateServiceCompleteTest {
  private val assessmentRepository: AssessmentRepository = mockk()
  private val episodeRepository: EpisodeRepository = mockk()
  private val authorService: AuthorService = mockk()
  private val auditService: AuditService = mockk()
  private val telemetryService: TelemetryService = mockk()

  private val assessmentUpdateService = AssessmentUpdateService(
    assessmentRepository,
    episodeRepository,
    authorService,
    auditService,
    telemetryService,
  )

  @Test
  fun `complete episode`() {
    val assessment = assessmentEntity()
    justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
    justRun {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_COMPLETE,
        any(),
        any(),
        any(),
        any(),
        any(),
        any(),
      )
    }

    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    val assessmentEpisode = assessment.episodes.first()
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    val episode = assessmentUpdateService.completeEpisode(assessmentEpisode)

    verify(exactly = 1) { episodeRepository.save(any()) }
    assertThat(episode.ended).isEqualToIgnoringMinutes(LocalDateTime.now())
  }

  @Test
  fun `audit complete episode`() {
    val assessment = assessmentEntity()
    justRun { auditService.createAuditEvent(any(), any(), any(), any(), any(), any()) }
    justRun {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_COMPLETE,
        any(),
        any(),
        any(),
        any(),
        any(),
        any(),
      )
    }
    every { assessmentRepository.findByAssessmentUuid(any()) } returns assessment
    every { episodeRepository.save(any()) } returns assessment.episodes[0]
    val assessmentEpisode = assessment.episodes.first()
    val author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name")
    every { authorService.getOrCreateAuthor() } returns author

    val episode = assessmentUpdateService.completeEpisode(assessmentEpisode)
    verify(exactly = 1) {
      auditService.createAuditEvent(
        AuditType.ARN_ASSESSMENT_COMPLETED,
        episode.assessmentUuid,
        episode.episodeUuid,
        assessment.subject?.crn,
        author,
        any(),
      )
    }
    verify(exactly = 1) {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_COMPLETE,
        assessment.subject?.crn!!,
        author,
        episode.assessmentUuid,
        episode.episodeUuid!!,
        AssessmentType.UPW,
      )
    }
  }

  private fun assessmentEntity(): AssessmentEntity {
    val subject = SubjectEntity(
      dateOfBirth = LocalDate.of(1989, 1, 1),
      crn = "X1345",
    )
    val episodes = mutableListOf<AssessmentEpisodeEntity>()
    val assessment = AssessmentEntity(
      assessmentUuid = UUID.fromString("7b4de6d5-4488-4c29-a909-7d3fdf15393d"),
      assessmentId = 1,
      episodes = episodes,
      subject = subject,
    )
    episodes.add(
      AssessmentEpisodeEntity(
        episodeUuid = UUID.fromString("669cdd10-1061-42ec-90d4-e34baab19566"),
        episodeId = 1234,
        assessment = assessment,
        assessmentType = AssessmentType.UPW,
        changeReason = "Change of Circs 2",
        createdDate = LocalDateTime.now(),
        author = AuthorEntity(userId = "1", userName = "USER", userAuthSource = "source", userFullName = "full name"),
      ),
    )
    return assessment
  }
}
