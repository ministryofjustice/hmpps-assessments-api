package uk.gov.justice.digital.assessments.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import net.minidev.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.GroupQuestionDto
import uk.gov.justice.digital.assessments.api.TableQuestionDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.CloneAssessmentExcludedQuestionsRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSource
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import uk.gov.justice.digital.assessments.services.exceptions.CrnIsMandatoryException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalSourceEndpointIsMandatoryException
import java.time.LocalDateTime

@Service
@Transactional("refDataTransactionManager")
class EpisodeService(
  private val questionService: QuestionService,
  private val courtCaseRestClient: CourtCaseRestClient,
  private val communityApiRestClient: CommunityApiRestClient,

  private val assessmentSchemaService: AssessmentSchemaService,
  private val cloneAssessmentExcludedQuestionsRepository: CloneAssessmentExcludedQuestionsRepository
) {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    const val cloneEpisodeOffset: Long = 55
    private const val fieldTypeTable = "table"
    private const val fieldTypeTableQuestion = "table_question"
    val tableFieldTypes: List<String> = listOf(fieldTypeTable, fieldTypeTableQuestion)
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())
  }

  fun prepopulateFromExternalSources(
    episode: AssessmentEpisodeEntity,
    assessmentSchemaCode: AssessmentSchemaCode
  ): AssessmentEpisodeEntity {
    val questionsToPopulate = questionService.getAllQuestions().withExternalSource(assessmentSchemaCode)
    if (questionsToPopulate.isEmpty())
      return episode

    questionsToPopulate
      .groupBy { it.externalSource }
      .forEach {
        prepopulateFromSource(episode, it.key, it.value)
      }

    return episode
  }

  fun prepopulateFromPreviousEpisodes(
    newEpisode: AssessmentEpisodeEntity,
    previousEpisodes: List<AssessmentEpisodeEntity>
  ): AssessmentEpisodeEntity {

    val orderedPreviousEpisodes = previousEpisodes.filter {
      it.endDate?.isAfter(LocalDateTime.now().minusWeeks(cloneEpisodeOffset)) ?: false && it.isComplete()
    }
      .sortedByDescending { it.endDate }

    val questions =
      assessmentSchemaService.getQuestionsForSchemaCode(newEpisode.assessmentSchemaCode)

    val ignoredQuestionCodes = cloneAssessmentExcludedQuestionsRepository
      .findAllByAssessmentSchemaCode(newEpisode.assessmentSchemaCode).map { it.questionCode }

    val questionCodes = questions.filterIsInstance<GroupQuestionDto>()
      .map { it.questionCode }
      .filterNot { ignoredQuestionCodes.contains(it) }

    val tableCodes = questions.filterIsInstance<TableQuestionDto>()
      .map { it.tableCode }
      .filterNot { ignoredQuestionCodes.contains(it) }

    orderedPreviousEpisodes.forEach { episode ->
      val relevantAnswers = episode.answers.filter { questionCodes.contains(it.key) }
      relevantAnswers.forEach {
        newEpisode.answers.putIfAbsent(it.key, it.value)
      }

      val relevantTables = episode.tables.filter { tableCodes.contains(it.key) }
      relevantTables.forEach {
        newEpisode.tables.putIfAbsent(it.key, it.value)
      }
    }
    return newEpisode
  }

  private fun prepopulateFromSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    questionSchemas: List<ExternalSourceQuestionSchemaDto>
  ) {
    val (structuredQuestions, simpleQuestions)  = questionSchemas.partition { it.fieldType in tableFieldTypes  }

    simpleQuestions.groupBy { it.externalSourceEndpoint }
      .forEach {
        val sourceData = loadSource(episode, sourceName, it.key) ?: return

        it.value.forEach { question ->
          episode.addAnswer(question.questionCode, getAnswersFromSourceData(sourceData, question))
        }
      }

    structuredQuestions.groupBy { it.externalSourceEndpoint }
      .forEach {
        val sourceData = loadSource(episode, sourceName, it.key) ?: return

        it.value.filter{ question -> question.fieldType == "table" }
          .forEach { tableQuestion ->
          val childQuestions = getChildQuestions(tableQuestion.questionCode, it.value)
          episode.addAnswer(tableQuestion.questionCode, getStructuredAnswersFromSourceData(sourceData, tableQuestion, childQuestions))
        }
      }
  }

  private fun getAnswersFromSourceData(source: DocumentContext, question: ExternalSourceQuestionSchemaDto): List<String> {
      return answerFormat(source, question).orEmpty()
  }

  fun getStructuredAnswersFromSourceData(
    sourceData: DocumentContext,
    questionSchema: ExternalSourceQuestionSchemaDto,
    childQuestions: List<ExternalSourceQuestionSchemaDto>
  ): List<String> {
    val answerData = sourceData.read<JSONArray>(questionSchema.jsonPathField)
    return buildStructuredAnswers(childQuestions, answerData)
  }

  private fun getChildQuestions(parentQuestionCode: String, questions: List<ExternalSourceQuestionSchemaDto>): List<ExternalSourceQuestionSchemaDto> {
    return questions.filter {
      it.parentQuestionCode.equals(parentQuestionCode)
    }
  }

  private fun loadSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    sourceEndpoint: String?
  ): DocumentContext? {
    try {
      val rawJson = when (sourceName) {
        ExternalSource.COURT.name -> loadFromCourtCase(episode)
        ExternalSource.DELIUS.name -> loadFromDelius(episode, sourceEndpoint)
        else -> return null
      }
      return JsonPath.parse(rawJson)
    } catch (e: Exception) {
      return null
    }
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

  private fun answerFormat(source: DocumentContext, question: ExternalSourceQuestionSchemaDto): List<String>? {
    try {
      return when (question.fieldType) {
        "varchar" -> listOf(source.read<Any>(question.jsonPathField).toString())
        "date" -> listOf(
          (source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).first().toString().split('T')[0]
        )
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
        else -> listOf((source.read<JSONArray>(question.jsonPathField).filterNotNull() as List<String>).first().toString())
      }
    } catch (e: Exception) {
      return when (question.fieldType) {
        "yesno" -> null
        else -> null
      }
    }
  }

  fun buildStructuredAnswers(
    childQuestions: List<ExternalSourceQuestionSchemaDto>,
    answerData: JSONArray
  ): List<String> {
    return answerData.map {
      val answersJson = JSONObject(it as Map<String, String>).toJSONString()
      val answersDetailContext = JsonPath.parse(answersJson)

       objectMapper.writeValueAsString(childQuestions.associate { childQuestion ->
        val value = answersDetailContext.read<Any>(childQuestion.jsonPathField)
        childQuestion.questionCode to listOf(value)
      })
    }
  }
}
