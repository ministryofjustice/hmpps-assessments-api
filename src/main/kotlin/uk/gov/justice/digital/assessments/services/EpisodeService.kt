package uk.gov.justice.digital.assessments.services

import com.jayway.jsonpath.DocumentContext
import com.jayway.jsonpath.JsonPath
import net.minidev.json.JSONArray
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSource
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import uk.gov.justice.digital.assessments.services.exceptions.CrnIsMandatoryException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalSourceEndpointIsMandatoryException

@Service
@Transactional("refDataTransactionManager")
class EpisodeService(
  private val questionService: QuestionService,
  private val courtCaseRestClient: CourtCaseRestClient,
  private val communityApiRestClient: CommunityApiRestClient,
) {
  fun prepopulate(
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

  private fun prepopulateFromSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    questions: List<ExternalSourceQuestionSchemaDto>
  ) {

    val questionsByExternalSourceEndpoint = questions.groupBy { it.externalSourceEndpoint }

    questionsByExternalSourceEndpoint.forEach { it ->
      val source = loadSource(episode, sourceName, it.key) ?: return
      it.value.forEach { prepopulateQuestion(episode, source, it) }
    }
  }

  private fun prepopulateQuestion(
    episode: AssessmentEpisodeEntity,
    source: DocumentContext,
    question: ExternalSourceQuestionSchemaDto
  ) {

    val answer = answerFormat(source, question.jsonPathField, question.fieldType)
    answer?.let {
      episode.answers?.let {
        it[question.questionCode] = answer
      }
    }
  }

  private fun loadSource(episode: AssessmentEpisodeEntity, sourceName: String?, sourceEndpoint: String?): DocumentContext? {
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
    val crn = episode?.assessment?.subject?.crn
      ?: throw CrnIsMandatoryException("Crn not found for episode ${episode.episodeUuid}")
    val externalSourceEndpoint = sourceEndpoint
      ?: throw ExternalSourceEndpointIsMandatoryException("External source endpoint is mandatory for episode ${episode.episodeUuid}")
    return communityApiRestClient.getOffenderJson(crn, externalSourceEndpoint)
  }

  private fun answerFormat(source: DocumentContext, jsonPathField: String?, fieldType: String?): List<String>? {
    try {
      return when (fieldType) {
        "varchar" -> listOf(source.read<Object>(jsonPathField).toString())
        "date" -> listOf(
          (source.read<JSONArray>(jsonPathField).filterNotNull() as List<String>).first().toString().split('T')[0]
        )
        "time" -> listOf(
          (source.read<JSONArray>(jsonPathField).filterNotNull() as List<String>).first().toString().split('T')[1]
        )
        "toUpper" -> listOf(
          (source.read<JSONArray>(jsonPathField).filterNotNull() as List<String>).first().toString().uppercase()
        )
        "array" -> (source.read<JSONArray>(jsonPathField).filterNotNull() as List<String>)
        else -> listOf((source.read<JSONArray>(jsonPathField).filterNotNull() as List<String>).first().toString())
      }
    } catch (e: Exception) {
      return null
    }
  }
}
