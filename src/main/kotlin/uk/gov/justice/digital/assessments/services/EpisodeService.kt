package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import java.time.LocalDateTime

@Service
@Transactional("refDataTransactionManager")
class EpisodeService(
  private val communityApiRestClient: CommunityApiRestClient,
  private val assessmentReferenceDataService: AssessmentReferenceDataService,
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private const val cloneEpisodeOffset: Long = 55
    private val CONTACT_ADDRESS_FIELDS = listOf("contact_address_house_number", "contact_address_building_name", "contact_address_street_name", "contact_address_postcode", "contact_address_town_or_city", "contact_address_county", "contact_address_district")
  }

  fun prePopulateEpisodeFromDelius(
    episode: AssessmentEpisodeEntity,
    communityOffenderDto: CommunityOffenderDto

  ): AssessmentEpisodeEntity {
    log.info("Pre-populating episode from Delius")
    CommunityOffenderDto.from(communityOffenderDto, episode)
    return episode
  }

  fun prePopulateFromPreviousEpisodes(
    newEpisode: AssessmentEpisodeEntity,
    previousEpisodes: List<AssessmentEpisodeEntity>
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

    val tableCodes = questions.filterIsInstance<TableQuestionDto>()
      .map { it.tableCode }
      .filterNot { ignoredQuestionCodes.contains(it) }

    orderedPreviousEpisodes.forEach { episode ->
      val relevantAnswers = episode.answers.filter { questionCodes.contains(it.key) }
      relevantAnswers.forEach { answer ->
        log.info("Delius value for question code: ${answer.key} is: ${newEpisode.answers[answer.key]}")
        log.info("Question code: ${answer.key} has answer: ${answer.value} in previous episode.")
        if ((newEpisode.answers[answer.key] == null || newEpisode.answers[answer.key]?.isEmpty() == true) &&
          !isAddressPrePopulatedFromDelius(newEpisode.answers, answer)
        ) {
          newEpisode.answers.put(answer.key, answer.value)
        }
      }

      val relevantTables = episode.tables.filter { tableCodes.contains(it.key) }
      relevantTables.forEach {
        if (newEpisode.tables[it.key] == null || newEpisode.answers[it.key]?.isEmpty() == true) {
          newEpisode.tables.putIfAbsent(it.key, it.value)
        }
      }
    }
    return newEpisode
  }

  private fun isAddressField(questionCode: String): Boolean {
    return CONTACT_ADDRESS_FIELDS.contains(questionCode)
  }

  private fun isAddressPrePopulatedFromDelius(newEpisodeAnswers: Answers, previousEpisodeAnswer: Map.Entry<String, List<Any>>): Boolean {
    // only check for existence of a value for address fields
    return isAddressField(previousEpisodeAnswer.key) && newEpisodeAnswers[previousEpisodeAnswer.key]?.isEmpty() == true
  }
}
