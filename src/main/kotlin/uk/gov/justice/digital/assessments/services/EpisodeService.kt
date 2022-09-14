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
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerDependencyEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
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
  private val communityApiRestClient: CommunityApiRestClient,
  private val assessmentReferenceDataService: AssessmentReferenceDataService,
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    private const val cloneEpisodeOffset: Long = 55

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

    questionsToPopulate
      .groupBy { it.externalSource }
      .forEach { (sourceName, questionSchemas) ->
        prepopulateFromSource(episode, sourceName, questionSchemas)
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

  fun removeOrphanedAnswers(episode: AssessmentEpisodeEntity) {
    // TODO: This will be moved to the database as part of a follow-up PR
    val answerDependencies = listOf(
      AnswerDependencyEntity("placement_preference", "gender_identity", AnswerDependencyEntity.Operator.NOT, "MALE"),
      AnswerDependencyEntity("placement_preferences", "gender_identity", AnswerDependencyEntity.Operator.NOT, "MALE"),
      AnswerDependencyEntity("placement_preference_complete", "gender_identity", AnswerDependencyEntity.Operator.NOT, "MALE"),
      AnswerDependencyEntity("sex_change_details", "sex_change", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("history_sexual_offending_details", "history_sexual_offending", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("poses_risk_to_children_details", "poses_risk_to_children", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("violent_offences_details", "violent_offences", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("acquisitive_offending_details", "acquisitive_offending", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("sgo_identifier_details", "sgo_identifier", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("control_issues_details", "control_issues", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("history_of_hate_based_behaviour_details", "history_of_hate_based_behaviour", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("high_profile_person_details", "high_profile_person", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("additional_rosh_info_details", "additional_rosh_info", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("location_exclusion_criteria_details", "location_exclusion_criteria", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("restricted_placement_details", "restricted_placement", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("no_female_supervisor_details", "no_female_supervisor", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("no_male_supervisor_details", "no_male_supervisor", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("restrictive_orders_details", "restrictive_orders", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("risk_management_issues_individual_details", "risk_management_issues_individual", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("risk_management_issues_supervised_group_details", "risk_management_issues_supervised_group", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("alcohol_drug_issues_details", "alcohol_drug_issues", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("physical_disability_details", "physical_disability", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("learning_disability_details", "learning_disability", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("learning_difficulty_details", "learning_difficulty", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("mental_health_condition_details", "mental_health_condition", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("additional_disabilities_details", "additional_disabilities", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("disabilities_details", "disabilities", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("allergies_details", "allergies", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("loss_consciousness_details", "loss_consciousness", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("epilepsy_details", "epilepsy", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("pregnancy_pregnant_details", "pregnancy", AnswerDependencyEntity.Operator.IS, "PREGNANT"),
      AnswerDependencyEntity("pregnancy_recently_given_birth_details", "pregnancy", AnswerDependencyEntity.Operator.IS, "RECENTLY_GIVEN_BIRTH"),
      AnswerDependencyEntity("other_health_issues_details", "other_health_issues", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("travel_information_details", "travel_information", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("driving_licence", "travel_information", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("vehicle", "travel_information", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("public_transport", "travel_information", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("public_transport", "travel_information", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("caring_commitments_details", "caring_commitments", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("employment_education_details_fulltime", "employment_education", AnswerDependencyEntity.Operator.IS, "FULLTIME_EDUCATION_EMPLOYMENT"),
      AnswerDependencyEntity("employment_education_details_parttime", "employment_education", AnswerDependencyEntity.Operator.IS, "PARTTIME_EDUCATION_EMPLOYMENT"),
      AnswerDependencyEntity("reading_writing_difficulties_details", "reading_writing_difficulties", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("work_skills_details", "work_skills", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("future_work_plans_details", "future_work_plans", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("education_training_need_details", "education_training_need", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("education_training_need_details", "education_training_need", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("individual_commitment", "education_training_need", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("individual_commitment", "education_training_need", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("individual_commitment_details", "individual_commitment", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("eligibility_intensive_working_details", "eligibility_intensive_working", AnswerDependencyEntity.Operator.IS, "NO"),
      AnswerDependencyEntity("recommended_hours_start_order", "eligibility_intensive_working", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("recommended_hours_midpoint_order", "eligibility_intensive_working", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("twenty_eight_hours_working_week_details", "eligibility_intensive_working", AnswerDependencyEntity.Operator.IS, "YES"),
      AnswerDependencyEntity("active_carer_commitments_details", "active_carer_commitments", AnswerDependencyEntity.Operator.ANY, ""),
    )

    answerDependencies.groupBy { it.triggerQuestionCode }.forEach { (triggeringQuestion, dependentQuestions) ->
      val triggeringQuestionAnswer = episode.answers[triggeringQuestion].orEmpty()

      dependentQuestions.forEach { answerDependency ->
        val satisfiesDependency = when (answerDependency.operator) {
          AnswerDependencyEntity.Operator.NOT -> triggeringQuestionAnswer.contains(answerDependency.triggerAnswerValue).not()
          AnswerDependencyEntity.Operator.IS -> triggeringQuestionAnswer.contains(answerDependency.triggerAnswerValue)
          AnswerDependencyEntity.Operator.ANY -> triggeringQuestionAnswer.isNotEmpty()
        }

        if (!satisfiesDependency) {
          episode.answers[answerDependency.questionCode] = emptyList()
        }
      }
    }
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
    questions: List<ExternalSourceQuestionDto>
  ) {
    questions.groupBy { it.externalSourceEndpoint }
      .forEach {
        val sourceData = loadSource(episode, sourceName, it.key) ?: return

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

  private fun loadSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    sourceEndpoint: String?
  ): DocumentContext? {
    log.info("Fetching source answers for source: $sourceName")
    try {
      val rawJson = when (sourceName) {
        ExternalSource.DELIUS.name -> loadFromDelius(episode, sourceEndpoint)
        else -> return null
      }
      return JsonPath.parse(rawJson)
    } catch (e: Exception) {
      return null
    }
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
            (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<Int>).map { it.toString() }.toList()
          } else {
            source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>
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
