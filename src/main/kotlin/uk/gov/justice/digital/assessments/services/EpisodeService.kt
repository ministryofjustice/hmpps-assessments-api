package uk.gov.justice.digital.assessments.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.apache.commons.lang3.time.FastDateFormat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.CarerCommitmentsAnswerDto
import uk.gov.justice.digital.assessments.api.DisabilityAnswerDto
import uk.gov.justice.digital.assessments.api.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.GPDetailsAnswerDto
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Answers
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusDisabilityDto
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusPersonalCircumstanceDto
import uk.gov.justice.digital.assessments.restclient.communityapi.PersonalContact
import uk.gov.justice.digital.assessments.services.dto.ExternalSource
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionDto
import uk.gov.justice.digital.assessments.services.exceptions.CrnIsMandatoryException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalSourceAnswerException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalSourceEndpointIsMandatoryException
import java.time.LocalDateTime
import java.util.regex.Pattern

@Service
@Transactional("refDataTransactionManager")
class EpisodeService(
  private val questionService: QuestionService,
  private val courtCaseRestClient: CourtCaseRestClient,
  private val communityApiRestClient: CommunityApiRestClient,

  private val assessmentApiRestClient: AssessmentApiRestClient,
  private val assessmentReferenceDataService: AssessmentReferenceDataService,
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private const val cloneEpisodeOffset: Long = 55

    private const val OASYS_SOURCE_NAME = "OASYS"

    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())
    private val basicDatePattern = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}$")
    private val basicDateFormatter = FastDateFormat.getInstance("dd/MM/yyyy")
    private val iso8601DateFormatter = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private val CONTACT_ADDRESS_FIELDS = listOf("contact_address_house_number", "contact_address_building_name", "contact_address_street_name", "contact_address_postcode", "contact_address_town_or_city", "contact_address_county", "contact_address_district")
  }

  fun prePopulateFromExternalSources(
    episode: AssessmentEpisodeEntity,
    assessmentType: AssessmentType
  ): AssessmentEpisodeEntity {
    log.info("Pre-populating episode from external source for assessment type: $assessmentType")
    val questionsToPopulate = questionService.getAllQuestions().withExternalSource(assessmentType)
    if (questionsToPopulate.isEmpty())
      return episode

    val latestCompleteEpisodeEndDate = getLatestCompleteEpisodeEndDate(episode)

    questionsToPopulate
      .groupBy { it.externalSource }
      .forEach { (sourceName, questionSchemas) ->
        prepopulateFromSource(episode, sourceName, questionSchemas, latestCompleteEpisodeEndDate)
      }

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

  private fun prepopulateFromSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    questions: List<ExternalSourceQuestionDto>,
    latestCompleteEpisodeEndDate: LocalDateTime?
  ) {
    episode.prepopulatedFromOASys = sourceName == OASYS_SOURCE_NAME
    questions.groupBy { it.externalSourceEndpoint }
      .forEach {
        val sourceData = loadSource(episode, sourceName, it.key, latestCompleteEpisodeEndDate) ?: return

        it.value.forEach { question ->
          episode.addAnswer(question.questionCode, getAnswersFromSourceData(sourceData, question))
        }
      }
  }

  fun getAnswersFromSourceData(
    source: DocumentContext,
    question: ExternalSourceQuestionDto
  ): List<Any> {
    return answerFormat(source, question).orEmpty()
  }

  private fun getLatestCompleteEpisodeEndDate(newEpisode: AssessmentEpisodeEntity): LocalDateTime? {
    return newEpisode.assessment.episodes.filter { it.isComplete() }
      .sortedByDescending { it.endDate }
      .firstOrNull()?.endDate
  }

  private fun loadSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    sourceEndpoint: String?,
    latestCompleteEpisodeEndDate: LocalDateTime?
  ): DocumentContext? {
    log.info("Fetching source answers for source: $sourceName")
    try {
      val rawJson = when (sourceName) {
        ExternalSource.COURT.name -> loadFromCourtCase(episode)
        ExternalSource.DELIUS.name -> loadFromDelius(episode, sourceEndpoint)
        ExternalSource.OASYS.name -> loadFromOASys(episode, latestCompleteEpisodeEndDate)
        else -> return null
      }
      return JsonPath.parse(rawJson)
    } catch (e: Exception) {
      return null
    }
  }

  private fun loadFromOASys(episode: AssessmentEpisodeEntity, latestCompleteEpisodeEndDate: LocalDateTime?): String? {
    val crn = episode.assessment.subject?.crn
      ?: throw CrnIsMandatoryException("Crn not found for episode ${episode.episodeUuid}")
    return assessmentApiRestClient.getOASysLatestAssessment(
      crn = crn,
      status = listOf("SIGNED", "COMPLETE"),
      types = listOf("LAYER_1", "LAYER_3"),
      cutoffDate = latestCompleteEpisodeEndDate
    )
  }

  private fun loadFromCourtCase(episode: AssessmentEpisodeEntity): String? {
    if (episode.offence?.source != ExternalSource.COURT.name) return null
    val (courtCode, caseNumber) = episode.offence?.sourceId!!.split('|')
    return courtCaseRestClient.getCourtCaseJson(courtCode, caseNumber)
  }

  private fun loadFromDelius(episode: AssessmentEpisodeEntity, sourceEndpoint: String?): String? {
    val crn = episode.assessment.subject?.crn
      ?: throw CrnIsMandatoryException("Crn not found for episode ${episode.episodeUuid}")
    val externalSourceEndpoint = sourceEndpoint
      ?: throw ExternalSourceEndpointIsMandatoryException("External source endpoint is mandatory for episode ${episode.episodeUuid}")
    return communityApiRestClient.getOffenderJson(crn, externalSourceEndpoint)
  }

  private fun formatDate(source: DocumentContext, question: ExternalSourceQuestionDto): String {
    val dateStr = (source.read<JSONArray>(question.jsonPathField).filterNotNull()).first().toString()

    return if (basicDatePattern.matcher(dateStr).matches())
      iso8601DateFormatter.format(basicDateFormatter.parse(dateStr)) else dateStr
  }

  private fun answerFormat(source: DocumentContext, question: ExternalSourceQuestionDto): List<Any>? {
    try {
      return when (question.fieldType) {
        "varchar" -> listOf(source.read<Any>(question.jsonPathField).toString())
        "date" -> listOf(formatDate(source, question).split('T')[0])
        "time" -> listOf(
          (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).first().toString().split('T')[1]
        )
        "toUpper" -> listOf(
          (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).first().toString().uppercase()
        )
        "array" -> (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>)
        "yesno" -> {
          if ((source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).size > 0)
            listOf("YES")
          else
            null
        }
        "mapped" -> {
          if (
            !question.ifEmpty && (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).size > 0 ||
            question.ifEmpty && (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).size == 0
          )
            listOf(question.mappedValue.orEmpty())
          else
            emptyList()
        }
        "structured" -> { getStructuredAnswersFromSourceData(source, question) }
        else -> {
          val field = source.read<JSONArray>(question.jsonPathField)
          if (field.isEmpty()) {
            emptyList<String>()
          } else if (field[0] is Int) {
            listOf((source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<Integer>).first().toString())
          } else {
            listOf((source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).first().toString())
          }
        }
      }
    } catch (e: Exception) {
      log.error("Could not extract value from jsonpath field : ${question.jsonPathField}", e)
      return null
    }
  }

  fun getStructuredAnswersFromSourceData(
    sourceData: DocumentContext,
    structureQuestion: ExternalSourceQuestionDto,
  ): List<Any>? {
    return when (structureQuestion.questionCode) {
      "gp_details" -> {
        val personalContacts = getPersonalContactsFromJson(sourceData, structureQuestion)
        GPDetailsAnswerDto.from(personalContacts)
      }
      "emergency_contact_details" -> {
        val personalContacts = getPersonalContactsFromJson(sourceData, structureQuestion)
        EmergencyContactDetailsAnswerDto.from(personalContacts)
      }
      "active_disabilities" -> {
        val deliusDisabilities = getDisabilitiesFromJson(sourceData, structureQuestion)
        DisabilityAnswerDto.from(deliusDisabilities)
      }
      "active_carer_commitments" -> {
        val carerCommitments = getCarerCommitmentsFromJson(sourceData, structureQuestion)
        CarerCommitmentsAnswerDto.from(carerCommitments)
      }
      else -> throw ExternalSourceAnswerException("Question code: ${structureQuestion.questionCode} not recognised")
    }
  }

  private fun getPersonalContactsFromJson(
    sourceData: DocumentContext,
    structureQuestion: ExternalSourceQuestionDto
  ): List<PersonalContact> {
    val personalContactJson = sourceData.read<JSONArray>(structureQuestion.jsonPathField).toJSONString()
    return objectMapper.readValue(personalContactJson)
  }

  private fun getDisabilitiesFromJson(
    sourceData: DocumentContext,
    structureQuestion: ExternalSourceQuestionDto
  ): List<DeliusDisabilityDto> {
    val disabilitiesJson = sourceData.read<JSONArray>(structureQuestion.jsonPathField).toJSONString()
    return objectMapper.readValue(disabilitiesJson)
  }

  private fun getCarerCommitmentsFromJson(
    sourceData: DocumentContext,
    structureQuestion: ExternalSourceQuestionDto
  ): List<DeliusPersonalCircumstanceDto> {
    val carerCommitmentsJson = sourceData.read<JSONArray>(structureQuestion.jsonPathField).toJSONString()
    return objectMapper.readValue(carerCommitmentsJson)
  }
}
