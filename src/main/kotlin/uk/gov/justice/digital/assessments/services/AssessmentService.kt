package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDateTime
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
        log.info("Existing assessment found for supervision $supervisionId")
        return AssessmentDto.from(newAssessment)
    }

    @Transactional
    fun createNewEpisode(assessmentId: Long, reason: String): AssessmentEpisodeDto? {
        val assessment = assessmentRepository.findByIdOrNull(assessmentId) ?:
            throw EntityNotFoundException("Assessment $assessmentId not found")
        val episode = assessment.newEpisode(reason)
        log.info("new episode created for assessment $assessmentId")
        return AssessmentEpisodeDto.from(episode)
    }

    fun getAssessmentEpisodes(assessmentId: Long): Collection<AssessmentEpisodeDto>? {
        val assessment = assessmentRepository.findByIdOrNull(assessmentId) ?:
        throw EntityNotFoundException("Assessment $assessmentId not found")
        log.info("Found ${assessment.episodes.size} for assessment $assessmentId")
        return AssessmentEpisodeDto.from(assessment.episodes)
    }

    fun getCurrentAssessmentEpisode(assessmentId: Long): AssessmentEpisodeDto {
        val assessment = assessmentRepository.findByIdOrNull(assessmentId) ?:
        throw EntityNotFoundException("Assessment $assessmentId not found")
        return AssessmentEpisodeDto.from(assessment.getCurrentEpisode())?:
        throw EntityNotFoundException("No current Episode for $assessmentId")
    }
}
