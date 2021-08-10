package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class SubjectService(
  private val assessmentRepository: AssessmentRepository
) {

  fun getSubjectForAssessment(assessmentUuid: UUID): SubjectEntity {
    val assessmentEntity = (assessmentRepository.findByAssessmentUuid(assessmentUuid)
      ?: throw EntityNotFoundException("Assessment $assessmentUuid not found"))

    return assessmentEntity.subject ?: throw EntityNotFoundException("Subject not found for $assessmentUuid ")
  }
}