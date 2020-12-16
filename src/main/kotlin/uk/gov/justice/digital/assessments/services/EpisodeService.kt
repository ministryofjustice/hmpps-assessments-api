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
        // val assessment = episode.assessment
        //val subject = assessment?.subject

        val questionsToPopulate = questionService.getAllQuestions().filter { it.externalSource != null }
        if (questionsToPopulate.isEmpty())
            return episode

        val sources = questionsToPopulate.groupBy { it.externalSource!!.split(':').first() }
        sources.forEach { prepopulateFromSource(episode, it.key, it.value) }

        return episode
    }

    private fun prepopulateFromSource(episode: AssessmentEpisodeEntity, sourceName: String, questions: List<QuestionSchemaEntity>) {
        val source = loadSource(episode, sourceName)

        questions.forEach{ prepopulateQuestion(episode, source, it) }
    } // prepopulateFromSource

    private fun prepopulateQuestion(episode: AssessmentEpisodeEntity, source: JsonObject, question: QuestionSchemaEntity) {
        val lookupPath = question.externalSource!!.split(':')[1]
        val answer = source.lookup<String?>(lookupPath).firstOrNull()

        if (answer != null)
            episode.answers!![question.questionSchemaUuid] = AnswerEntity(answer, emptyMap())
    }

    private fun loadSource(episode: AssessmentEpisodeEntity, sourceName: String): JsonObject {
        val rawJson = courtCaseRestClient.getCourtCaseJson("SHF06", "668911253")
        return Parser.default().parse(StringBuilder(rawJson)) as JsonObject
    }
}