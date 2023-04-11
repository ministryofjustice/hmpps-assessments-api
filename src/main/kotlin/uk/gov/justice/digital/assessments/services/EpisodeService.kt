package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.groups.GroupQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import java.time.LocalDateTime

@Service
@Transactional("refDataTransactionManager")
class EpisodeService(
  private val assessmentReferenceDataService: AssessmentReferenceDataService,
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository,
  private val telemetryService: TelemetryService,
  private val auditService: AuditService,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private const val cloneEpisodeOffset: Long = 55
    private val CONTACT_ADDRESS_FIELDS = listOf("contact_address_house_number", "contact_address_building_name", "contact_address_street_name", "contact_address_postcode", "contact_address_town_or_city", "contact_address_county", "contact_address_district")
  }

  fun prePopulateEpisodeFromDelius(
    episode: AssessmentEpisodeEntity,
    caseDetails: CaseDetails?,

  ) {
    log.info("Pre-populating episode from Delius")
    if (caseDetails != null) {
      episode.updateFrom(caseDetails)
    }
  }

  fun prePopulateFromPreviousEpisodes(
    newEpisode: AssessmentEpisodeEntity,
    previousEpisodes: List<AssessmentEpisodeEntity>,
  ): AssessmentEpisodeEntity {
    log.debug("Entered prepopulateFromPreviousEpisodes")
    val orderedPreviousEpisodes = previousEpisodes.filter {
      it.endDate?.isAfter(LocalDateTime.now().minusWeeks(cloneEpisodeOffset)) ?: false && it.isComplete()
    }.sortedByDescending { it.endDate }

    val questions =
      assessmentReferenceDataService.getQuestionsForAssessmentType(newEpisode.assessmentType)

    val ignoredQuestionCodes = cloneAssessmentExcludedQuestionsRepository
      .findAllByAssessmentType(newEpisode.assessmentType).map { it.questionCode }

    val questionCodes = questions.filterIsInstance<GroupQuestionDto>()
      .map { it.questionCode }
      .filterNot { ignoredQuestionCodes.contains(it) }

    orderedPreviousEpisodes.firstOrNull()?.let { auditAndLogClonedAssessment(newEpisode, it) }

    orderedPreviousEpisodes.forEach { episode ->
      val relevantAnswers = episode.answers.filter { questionCodes.contains(it.key) }
      relevantAnswers.forEach { answer ->
        if ((newEpisodeAnswerIsNull(newEpisode, answer) || newEpisodeAnswerIsEmpty(newEpisode, answer)) &&
          !isAddressPrePopulatedFromDelius(newEpisode.answers, answer)
        ) {
          newEpisode.answers[answer.key] = answer.value
        }
      }
    }
    return newEpisode
  }

  private fun newEpisodeAnswerIsEmpty(
    newEpisode: AssessmentEpisodeEntity,
    answer: Map.Entry<String, List<Any>>,
  ) = newEpisode.answers[answer.key]?.isEmpty() == true

  private fun newEpisodeAnswerIsNull(
    newEpisode: AssessmentEpisodeEntity,
    answer: Map.Entry<String, List<Any>>,
  ) = newEpisode.answers[answer.key] == null

  private fun isAddressField(questionCode: String): Boolean {
    return CONTACT_ADDRESS_FIELDS.contains(questionCode)
  }

  private fun isAddressPrePopulatedFromDelius(newEpisodeAnswers: Answers, previousEpisodeAnswer: Map.Entry<String, List<Any>>): Boolean {
    // only check for existence of a value for address fields
    return isAddressField(previousEpisodeAnswer.key) && newEpisodeAnswers[previousEpisodeAnswer.key]?.isEmpty() == true
  }

  private fun auditAndLogClonedAssessment(episode: AssessmentEpisodeEntity, previousEpisode: AssessmentEpisodeEntity) {
    auditService.createAuditEvent(
      AuditType.ARN_ASSESSMENT_CLONED,
      episode.assessment.assessmentUuid,
      episode.episodeUuid,
      episode.assessment.subject?.crn,
      episode.author,
      mapOf(
        "previousEpisodeUUID" to previousEpisode.episodeUuid,
        "previousEpisodeCompletedDate" to previousEpisode.endDate!!,
      ),
    )
    episode.assessment.subject?.crn?.let {
      telemetryService.trackAssessmentClonedEvent(
        it,
        episode.author,
        episode.assessment.assessmentUuid,
        episode.episodeUuid,
        episode.assessmentType,
        previousEpisode.episodeUuid,
        previousEpisode.endDate!!,
      )
    }
  }
}
