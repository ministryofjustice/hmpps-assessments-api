package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.AnswersDto
import uk.gov.justice.digital.assessments.api.assessments.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.assessments.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.EpisodeRepository
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.services.exceptions.CannotCloseEpisodeException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import uk.gov.justice.digital.assessments.utils.AssessmentUtils
import java.time.LocalDateTime

@Service
class AssessmentUpdateService(
  private val assessmentRepository: AssessmentRepository,
  private val episodeRepository: EpisodeRepository,
  private val authorService: AuthorService,
  private val auditService: AuditService,
  private val telemetryService: TelemetryService,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Transactional("assessmentsTransactionManager")
  fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateEpisode(episode, updatedEpisodeAnswers.answers)
  }

  @Transactional("assessmentsTransactionManager")
  fun updateCurrentEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: UpdateAssessmentEpisodeDto
  ): AssessmentEpisodeDto {
    return updateEpisode(episode, updatedEpisodeAnswers.answers)
  }

  private fun updateEpisode(
    episode: AssessmentEpisodeEntity,
    updatedEpisodeAnswers: AnswersDto
  ): AssessmentEpisodeDto {
    if (episode.isComplete() || episode.isClosed())
      throw UpdateClosedEpisodeException("Cannot update a closed or completed Episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")

    episode.updateEpisodeAnswers(updatedEpisodeAnswers)
    AssessmentUtils.removeOrphanedAnswers(episode)

    val currentAuthor = episode.author
    episode.author = authorService.getOrCreateAuthor()
    episode.lastEditedDate = LocalDateTime.now()

    log.info("Updated episode ${episode.episodeUuid} with ${updatedEpisodeAnswers.size} answer(s) for assessment ${episode.assessment.assessmentUuid}")

    // shouldn't need this because of the transactional annotation, unless there is an exception which needs handling.
    assessmentRepository.save(episode.assessment)
    log.info("Saved episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")
    auditEpisodeUpdate(currentAuthor, episode)
    return AssessmentEpisodeDto.from(episode)
  }

  fun AssessmentEpisodeEntity.updateEpisodeAnswers(
    updatedEpisodeAnswers: AnswersDto
  ) {
    for (updatedAnswer in updatedEpisodeAnswers) {
      this.answers.let {
        it[updatedAnswer.key] = updatedAnswer.value
      }
    }
  }

  @Transactional("assessmentsTransactionManager")
  fun completeEpisode(
    episode: AssessmentEpisodeEntity
  ): AssessmentEpisodeDto {
    episode.author = authorService.getOrCreateAuthor()
    episode.lastEditedDate = LocalDateTime.now()

    if (!episode.isComplete()) {
      episode.complete()
      episodeRepository.save(episode)
      auditAndLogCompleteAssessment(episode)
      log.info("Saved completed episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")
    }
    return AssessmentEpisodeDto.from(episode)
  }

  @Transactional("assessmentsTransactionManager")
  fun closeEpisode(
    episode: AssessmentEpisodeEntity
  ): AssessmentEpisodeDto {
    if (!episode.isComplete()) {
      episode.author = authorService.getOrCreateAuthor()
      episode.close()
      episodeRepository.save(episode)

      auditAndLogClosedAssessment(episode)
      log.info("Closed episode ${episode.episodeUuid} for assessment ${episode.assessment.assessmentUuid}")
    } else throw CannotCloseEpisodeException("Cannot close a completed episode", "Episode already completed on ${episode.endDate}")

    return AssessmentEpisodeDto.from(episode)
  }

  private fun auditEpisodeUpdate(currentAuthor: AuthorEntity, episode: AssessmentEpisodeEntity) {
    auditService.createAuditEvent(
      AuditType.ARN_ASSESSMENT_UPDATED,
      episode.assessment.assessmentUuid,
      episode.episodeUuid,
      episode.assessment.subject?.crn,
      episode.author
    )
    if (currentAuthor != episode.author) {
      auditService.createAuditEvent(
        AuditType.ARN_ASSESSMENT_REASSIGNED,
        episode.assessment.assessmentUuid,
        episode.episodeUuid,
        episode.assessment.subject?.crn,
        episode.author,
        mapOf("assignedFrom" to currentAuthor.userName, "assignedTo" to episode.author.userName)
      )
      episode.assessment.subject?.crn?.let {
        telemetryService.trackAssessmentEvent(
          TelemetryEventType.ASSESSMENT_REALLOCATED,
          it,
          episode.author,
          episode.assessment.assessmentUuid,
          episode.episodeUuid,
          episode.assessmentType
        )
      }
    }
  }

  private fun auditAndLogCompleteAssessment(episode: AssessmentEpisodeEntity) {
    auditService.createAuditEvent(
      AuditType.ARN_ASSESSMENT_COMPLETED,
      episode.assessment.assessmentUuid,
      episode.episodeUuid,
      episode.assessment.subject?.crn,
      episode.author
    )
    episode.assessment.subject?.crn?.let {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_COMPLETE,
        it,
        episode.author,
        episode.assessment.assessmentUuid,
        episode.episodeUuid,
        episode.assessmentType
      )
    }
  }

  private fun auditAndLogClosedAssessment(episode: AssessmentEpisodeEntity) {
    auditService.createAuditEvent(
      AuditType.ARN_ASSESSMENT_CLOSED,
      episode.assessment.assessmentUuid,
      episode.episodeUuid,
      episode.assessment.subject?.crn,
      episode.author
    )
    episode.assessment.subject?.crn?.let {
      telemetryService.trackAssessmentEvent(
        TelemetryEventType.ASSESSMENT_CLOSED,
        it,
        episode.author,
        episode.assessment.assessmentUuid,
        episode.episodeUuid,
        episode.assessmentType
      )
    }
  }
}
