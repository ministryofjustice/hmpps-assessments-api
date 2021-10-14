package uk.gov.justice.digital.assessments.services

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.lookup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.services.dto.ExternalSourceQuestionSchemaDto
import java.lang.StringBuilder

@Service
@Transactional("refDataTransactionManager")
class EpisodeService(
  private val questionService: QuestionService,
  private val courtCaseRestClient: CourtCaseRestClient
) {
  fun prepopulate(
    episode: AssessmentEpisodeEntity,
    assessmentSchemaCode: AssessmentSchemaCode
  ): AssessmentEpisodeEntity {
    val questionsToPopulate = questionService.getAllQuestions().withExternalSource(assessmentSchemaCode)
    if (questionsToPopulate.isEmpty())
      return episode

    val sources = questionsToPopulate.groupBy { it ->
      it.externalSource
    }
    sources.forEach { prepopulateFromSource(episode, it.key, it.value) }

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
    val rawAnswer = source.lookup<String?>(question.jsonPathField).firstOrNull() ?: return

    val answer = answerFormat(rawAnswer, question.fieldType)
    episode.answers?.let {
      it[question.questionCode] = listOf(answer)
    }
  }

  private fun loadSource(episode: AssessmentEpisodeEntity, sourceName: String?): JsonObject? {
    try {
      val rawJson = when (sourceName) {
        "COURT" -> loadFromCourtCase(episode)
        else -> return null
      }
      return Parser.default().parse(StringBuilder(rawJson)) as JsonObject
    } catch (e: Exception) {
      return null
    }
  }

  private fun loadFromCourtCase(episode: AssessmentEpisodeEntity): String? {
    if (episode.offence?.source != "COURT") return null
    val (courtCode, caseNumber) = episode.offence?.sourceId!!.split('|')
    return courtCaseRestClient.getCourtCaseJson(courtCode, caseNumber)
  }

  private fun answerFormat(rawAnswer: String, format: String?): String {
    return when (format) {
      "date" -> rawAnswer.split('T')[0]
      "time" -> rawAnswer.split('T')[1]
      "toUpper" -> rawAnswer.uppercase()
      else -> rawAnswer
    }
  }
}
