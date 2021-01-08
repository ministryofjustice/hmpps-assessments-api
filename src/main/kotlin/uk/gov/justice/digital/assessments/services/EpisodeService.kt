package uk.gov.justice.digital.assessments.services

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.lookup
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import java.lang.StringBuilder

@Service
class EpisodeService(
        private val questionService: QuestionService,
        private val courtCaseRestClient: CourtCaseRestClient
) {
    fun prepopulate(episode: AssessmentEpisodeEntity): AssessmentEpisodeEntity {
        val questionsToPopulate = questionService.getAllQuestions().filter { it.externalSource != null }
        if (questionsToPopulate.isEmpty())
            return episode

        val sources = questionsToPopulate.groupBy { it.externalSource!!.split(':').first() }
        sources.forEach { prepopulateFromSource(episode, it.key, it.value) }

        return episode
    }

    private fun prepopulateFromSource(episode: AssessmentEpisodeEntity, sourceName: String, questions: List<QuestionSchemaEntity>) {
        val source = loadSource(episode, sourceName)?: return

        questions.forEach{ prepopulateQuestion(episode, source, it) }
    } // prepopulateFromSource

    private fun prepopulateQuestion(episode: AssessmentEpisodeEntity, source: JsonObject, question: QuestionSchemaEntity) {
        val (_, lookupPath, format) = "${question.externalSource}:".split(':')
        val rawAnswer = source.lookup<String?>(lookupPath).firstOrNull()?: return

        val answer = answerFormat(rawAnswer, format)
        episode.answers!![question.questionSchemaUuid] = AnswerEntity(answer, emptyMap())
    }

    private fun loadSource(episode: AssessmentEpisodeEntity, sourceName: String): JsonObject? {
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

        val (courtCode, caseNumber) = subject?.sourceId!!.split('|')
        return courtCaseRestClient.getCourtCaseJson(courtCode, caseNumber)
    }

    private fun answerFormat(rawAnswer: String, format: String?): String {
        when (format) {
            "date" -> return rawAnswer.split('T')[0]
            "time" -> return rawAnswer.split('T')[1]
            "forename" -> return rawAnswer.split(' ')[0]
            "surname" -> return rawAnswer.split(' ')[1]
            "toUpper" -> return rawAnswer.toUpperCase()
            else -> return rawAnswer
        }
    }
}