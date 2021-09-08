package uk.gov.justice.digital.assessments.services

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.lookup
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import java.lang.StringBuilder

@Service
@Transactional("assessmentsTransactionManager")
class EpisodeService(
  private val questionService: QuestionService,
  private val courtCaseRestClient: CourtCaseRestClient
) {
  fun prepopulate(episode: AssessmentEpisodeEntity): AssessmentEpisodeEntity {
    val questionsToPopulate = questionService.getAllQuestions().withExternalSource()
    if (questionsToPopulate.isEmpty())
      return episode

    val sources = questionsToPopulate.groupBy { it.externalSource?.split(':')?.first() }
    sources.forEach { prepopulateFromSource(episode, it.key, it.value) }

    return episode
  }

  private fun prepopulateFromSource(episode: AssessmentEpisodeEntity, sourceName: String?, questions: List<QuestionSchemaEntity>) {
    val source = loadSource(episode, sourceName) ?: return

    questions.forEach { prepopulateQuestion(episode, source, it) }
  }

  private fun prepopulateQuestion(episode: AssessmentEpisodeEntity, source: JsonObject, question: QuestionSchemaEntity) {
    val (_, lookupPath, format) = "${question.externalSource}:".split(':')
    val rawAnswer = source.lookup<String?>(lookupPath).firstOrNull() ?: return

    val answer = answerFormat(rawAnswer, format)
    episode.answers?.set(question.questionCode, AnswerEntity.from(answer))
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
    val subject = episode.assessment?.subject
    if (subject?.source != "COURT") return null

    val (courtCode, caseNumber) = subject.sourceId!!.split('|')
    return courtCaseRestClient.getCourtCaseJson(courtCode, caseNumber)
  }

  private fun answerFormat(rawAnswer: String, format: String?): String {
    return when (format) {
      "date" -> rawAnswer.split('T')[0]
      "time" -> rawAnswer.split('T')[1]
      "toUpper" -> rawAnswer.toUpperCase()
      else -> rawAnswer
    }
  }
}
