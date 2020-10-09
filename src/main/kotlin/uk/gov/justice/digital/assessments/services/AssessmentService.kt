package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
import java.util.*
import javax.transaction.Transactional

@Service
class AssessmentService(private val assessmentRepository: AssessmentRepository) {

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
        val assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
                ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
        val episode = assessment.newEpisode(reason)
        log.info("New episode created for assessment $assessmentUuid")
        return AssessmentEpisodeDto.from(episode)
    }

    fun getAssessmentEpisodes(assessmentUuid: UUID): Collection<AssessmentEpisodeDto>? {
        val assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
                ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
        log.info("Found ${assessment.episodes.size} for assessment $assessmentUuid")
        return AssessmentEpisodeDto.from(assessment.episodes)
    }

    fun getCurrentAssessmentEpisode(assessmentUuid: UUID): AssessmentEpisodeDto {
        val assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
                ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
        return AssessmentEpisodeDto.from(assessment.getCurrentEpisode())
                ?: throw EntityNotFoundException("No current Episode for $assessmentUuid")
    }

    @Transactional
    fun updateEpisode(assessmentUuid: UUID, episodeUuid: UUID, updatedEpisodeAnswers: UpdateAssessmentEpisodeDto): AssessmentEpisodeDto? {
        val assessment = assessmentRepository.findByAssessmentUuid(assessmentUuid)
                ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")

        val episode = assessment.episodes.first { it.episodeUuid == episodeUuid }

        for (updatedAnswer in updatedEpisodeAnswers.answers ) {
            val currentQuestionAnswer = episode.answers[updatedAnswer.key]

            if(currentQuestionAnswer == null) {
                episode.answers[updatedAnswer.key] = AnswerEntity(updatedAnswer.value.freeTextAnswer, updatedAnswer.value.answers)
            }
            else {
                currentQuestionAnswer.freeTextAnswer = updatedAnswer.value.freeTextAnswer
                currentQuestionAnswer.answers = updatedAnswer.value.answers
            }
        }
        log.info("Updated episode $episodeUuid with ${updatedEpisodeAnswers.answers.size} answer(s) for assessment $assessmentUuid")
        return AssessmentEpisodeDto.from(episode)
    }
}