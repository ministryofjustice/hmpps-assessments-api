package uk.gov.justice.digital.assessments.services

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.lookup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSource
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import uk.gov.justice.digital.assessments.services.exceptions.CrnIsMandatoryException
import java.lang.StringBuilder

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
      .forEach { prepopulateFromSource(episode, it.key, it.value) }

    return episode
  }

  private fun prepopulateFromSource(
    episode: AssessmentEpisodeEntity,
    sourceName: String?,
    questions: List<ExternalSourceQuestionSchemaDto>
  ) {
    val source = loadSource(episode, sourceName) ?: return

    questions.forEach { prepopulateQuestion(episode, source, it) }
  }

  private fun prepopulateQuestion(
    episode: AssessmentEpisodeEntity,
    source: JsonObject,
    question: ExternalSourceQuestionSchemaDto
  ) {
    val rawAnswers = source.lookup<Object>(question.jsonPathField).filterNotNull()
    if (rawAnswers.isEmpty()) return

    val answer = answerFormat(rawAnswers, question.fieldType)
    episode.answers?.let {
      it[question.questionCode] = answer
    }
  }

  private fun loadSource(episode: AssessmentEpisodeEntity, sourceName: String?): JsonObject? {
    try {
      val rawJson = when (sourceName) {
        ExternalSource.COURT.name -> loadFromCourtCase(episode)
        ExternalSource.DELIUS.name -> loadFromDelius(episode)
        else -> return null
      }
      return Parser.default().parse(StringBuilder(rawJson)) as JsonObject
    } catch (e: Exception) {
      return null
    }
  }

  private fun loadFromCourtCase(episode: AssessmentEpisodeEntity): String? {
    if (episode.offence?.source != ExternalSource.COURT.name) return null
    val (courtCode, caseNumber) = episode.offence?.sourceId!!.split('|')
    return courtCaseRestClient.getCourtCaseJson(courtCode, caseNumber)
  }

  private fun loadFromDelius(episode: AssessmentEpisodeEntity): String? {
    val crn = episode?.assessment?.subject?.crn
      ?: throw CrnIsMandatoryException("Crn not found for episode ${episode.episodeUuid}")
    return communityApiRestClient.getOffenderJson(crn)
  }

  private fun answerFormat(rawAnswer: List<Object>, format: String?): List<String> {
    return when (format) {
      "date" -> listOf(rawAnswer.first().toString().split('T')[0])
      "time" -> listOf(rawAnswer.first().toString().split('T')[1])
      "yyyy-mm-dd" -> listOf(
        "${rawAnswer[0]}-${rawAnswer[1]}-${rawAnswer[2]}"
      )
      "toUpper" -> listOf(rawAnswer.first().toString().uppercase())
      "array" -> rawAnswer as List<String>
      else -> listOf(rawAnswer.first().toString())
    }
  }
}
