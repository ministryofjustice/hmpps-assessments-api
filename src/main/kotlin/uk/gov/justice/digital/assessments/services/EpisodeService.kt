package uk.gov.justice.digital.assessments.services

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
    val cloneEpisodeOffset: Long = 55
    private val tableFieldTypes: List<String> = listOf("table", "table_question")

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
      .map { it as GroupQuestionDto }
      .map { it.questionCode }
      .filterNot { ignoredQuestionCodes.contains(it) }

    val tableCodes = questions.filterIsInstance<TableQuestionDto>()
      .map { it as TableQuestionDto }
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
    questions: List<ExternalSourceQuestionSchemaDto>
  ) {

    val questionsByExternalSourceEndpoint = questions.groupBy { it.externalSourceEndpoint }

    questionsByExternalSourceEndpoint.forEach {
      val source = loadSource(episode, sourceName, it.key) ?: return

      it.value.forEach { question ->
        val childQuestions = getChildQuestions(question.questionCode, it.value)
        prepopulateQuestion(episode, source, question, childQuestions)
      }
    }
  }

  private fun prepopulateQuestion(
    episode: AssessmentEpisodeEntity,
    source: DocumentContext,
    question: ExternalSourceQuestionSchemaDto
  ) {

    val answer = answerFormat(source, question).orEmpty()
    episode.answers.let {
      it[question.questionCode] = it[question.questionCode].orEmpty().plus(answer).toSet().toList()
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

  private fun tableFormat(source: DocumentContext, question: ExternalSourceQuestionSchemaDto, childQuestions: List<ExternalSourceQuestionSchemaDto>): TableRows {
    return when (question.fieldType) {
      "table" -> buildTable(source, question, childQuestions)
      else -> return emptyList<TableRow>() as TableRows
    }
  }

  fun buildTable(
    source: DocumentContext,
    parentQuestion: ExternalSourceQuestionSchemaDto,
    childQuestions: List<ExternalSourceQuestionSchemaDto>
  ): TableRows {
    val contactDetails = source.read<JSONArray>(parentQuestion.jsonPathField)

    val tableRows = contactDetails.map {
      val contactDetail = it as Map<String, Any>
      val contactDetailJson = JSONObject(contactDetail).toJSONString()
      val contactDetailContext = JsonPath.parse(contactDetailJson)

      childQuestions.map { childQuestion ->
        val value = contactDetailContext.read<Any>(childQuestion.jsonPathField)
        childQuestion.questionCode to listOf(value)
      }.toMap()
    }
    return tableRows as TableRows
  }




//  private fun prepopulateFromSource(
//    episode: AssessmentEpisodeEntity,
//    sourceName: String?,
//    questions: List<ExternalSourceQuestionSchemaDto>
//  ) {
//    // filter (partition) endpoints for tables and table rows
//    val (tableQuestions, notTableQuestions) = questions.partition{ it.fieldType == "table" || it.fieldType == "table_question" }
//
//    val questionsByExternalSourceEndpoint = notTableQuestions.groupBy { it.externalSourceEndpoint }
//    questionsByExternalSourceEndpoint.forEach {
//        it -> val source = loadSource(episode, sourceName, it.key) ?: return
//
//      it.value.forEach { question ->
//        val childQuestions = getChildQuestions(question.questionCode, it.value)
//        prepopulateQuestion(episode, source, question, childQuestions)
//      }
//    }
//    //TODO prepopulate tables
//    prepopulateQuestionTables(episode, sourceName, tableQuestions)
//  }
//
//  private fun prepopulateQuestionTables(
//    episode: AssessmentEpisodeEntity,
//    sourceName: String?,
//    tableQuestions: List<ExternalSourceQuestionSchemaDto>
//  ) {
//    val (tables, questions) = tableQuestions.partition{ it.fieldType == "table" }
//
//    val gpQuestionCode = "gp_details"
////    val gpTable = tables.filter { it.questionCode == gpQuestionCode }[0]
//    val gpJson = loadSource(episode, sourceName, gpQuestionCode) ?: return
//
//    questions.filter { it.parentQuestionCode == gpQuestionCode }
//      .forEach { question ->       }
//
//
//
//      table -> val json = loadSource(episode, sourceName, table.questionCode) ?: return
//      val tableRows = questionCodes[table.questionCode]
//
//    TODO("Not yet implemented")
//  }


  //
//  fun buildTable(
//    source: DocumentContext,
//    question: ExternalSourceQuestionSchemaDto,
//    childQuestions: List<ExternalSourceQuestionSchemaDto>
//  ) : List<String>? {
//    val contactDetails = source.read<JSONArray>(question.jsonPathField)
//       contactDetails.size
//
////    val contactDetailJson = contactDetails
//
//    val pathFields = childQuestions.map { it ->
//      it.jsonPathField
//    }
//
//    pathFields.map {
//      val detailObject = contactDetails[0] as JSONObject
//      detailObject.
//    }
////    val  = childQuestions.map { it ->
////      source.read<JSONObject>(it.externalSource)
////    }
//    return listOf()
//  }
}
