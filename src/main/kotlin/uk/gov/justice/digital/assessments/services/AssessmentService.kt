package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import uk.gov.justice.digital.assessments.api.AssessmentAnswersDto
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
class AssessmentService(private val assessmentRepository: AssessmentRepository, private val questionService: QuestionService) {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun createNewAssessment(supervisionId: String?): AssessmentDto {
        val existingAssessment = assessmentRepository.findBySupervisionId(supervisionId)

        if (existingAssessment != null) {
            log.info("Existing assessment found for supervision $supervisionId")
            return AssessmentDto.from(existingAssessment)
        }

        val newAssessment = assessmentRepository.save(AssessmentEntity(supervisionId = supervisionId, createdDate = LocalDateTime.now()))
        log.info("New assessment created for supervision $supervisionId")
        return AssessmentDto.from(newAssessment)
    }

    @Transactional
    fun createNewEpisode(assessmentUuid: UUID, reason: String): AssessmentEpisodeDto? {
        val assessment = getAssessmentByUuid(assessmentUuid)
        val episode = assessment.newEpisode(reason)
        log.info("New episode created for assessment $assessmentUuid")
        return AssessmentEpisodeDto.from(episode)
    }

    fun getAssessmentEpisodes(assessmentUuid: UUID): Collection<AssessmentEpisodeDto>? {
        val assessment = getAssessmentByUuid(assessmentUuid)
        log.info("Found ${assessment.episodes.size} for assessment $assessmentUuid")
        return AssessmentEpisodeDto.from(assessment.episodes)
    }

    fun getCurrentAssessmentEpisode(assessmentUuid: UUID): AssessmentEpisodeDto {
        val assessment = getAssessmentByUuid(assessmentUuid)
        return AssessmentEpisodeDto.from(assessment.getCurrentEpisode())
                ?: throw EntityNotFoundException("No current Episode for $assessmentUuid")
    }

    fun getCurrentAssessmentCodedAnswers(assessmentUuid: UUID) : AssessmentAnswersDto {
        val questionCodes: Map<UUID, String?> =  questionService.getAllQuestions().map { it.questionSchemaUuid to it.questionCode }.toMap()
        val answerCodes: Map<UUID, String?> =  questionService.getAllAnswers().map { it.answerSchemaUuid to it.answerSchemaCode }.toMap()
        val assessment = getAssessmentByUuid(assessmentUuid)
        val answers: MutableMap<String, Set<String>> = mutableMapOf()

        assessment.episodes.sortedWith(compareBy( nullsLast()) {it.endDate }).forEach { episode ->
            if (episode.answers !=null) {
                episode.answers!!.forEach { answer ->
                    val questionCode = questionCodes[answer.key] ?:
                        throw IllegalStateException("Question Code not found for UUID ${answer.key}")
                    val answerCode = answer.value.answers.map { answerCodes[it.key] ?:
                        throw IllegalStateException("Answer Code not found for UUID ${it.key}") }.toSet()

                    if( answerCode.isNotEmpty()) {
                        answers[questionCode] = answerCode
                    }
                }
            }
        }
        return AssessmentAnswersDto(assessmentUuid, answers)
    }

    @Transactional
    fun updateEpisode(assessmentUuid: UUID, episodeUuid: UUID, updatedEpisodeAnswers: UpdateAssessmentEpisodeDto): AssessmentEpisodeDto? {
        val episode = getEpisode(episodeUuid, assessmentUuid)

        if (episode.isClosed()) throw UpdateClosedEpisodeException("Cannot update closed Episode $episodeUuid")

        for (updatedAnswer in updatedEpisodeAnswers.answers) {
            val currentQuestionAnswer = episode.answers?.get(updatedAnswer.key)

            if (currentQuestionAnswer == null) {
                episode.answers?.put(updatedAnswer.key, AnswerEntity(updatedAnswer.value.freeTextAnswer, updatedAnswer.value.answers))
            } else {
                currentQuestionAnswer.freeTextAnswer = updatedAnswer.value.freeTextAnswer
                currentQuestionAnswer.answers = updatedAnswer.value.answers
            }
        }
        log.info("Updated episode $episodeUuid with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment $assessmentUuid")
        return AssessmentEpisodeDto.from(episode)
    }

    private fun getEpisode(episodeUuid: UUID, assessmentUuid: UUID): AssessmentEpisodeEntity {
        return getAssessmentByUuid(assessmentUuid).episodes.firstOrNull { it.episodeUuid == episodeUuid }
                ?: throw EntityNotFoundException("No Episode $episodeUuid for $assessmentUuid")
    }

    private fun getAssessmentByUuid(assessmentUuid: UUID): AssessmentEntity {
        return assessmentRepository.findByAssessmentUuid(assessmentUuid)
                ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
    }
}