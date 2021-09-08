package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class SubjectService(
  private val assessmentRepository: AssessmentRepository
) {

  fun getSubjectForAssessment(assessmentUuid: UUID): SubjectEntity {
    val assessmentEntity = (
      assessmentRepository.findByAssessmentUuid(assessmentUuid)
        ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
      )

    return assessmentEntity.subject ?: throw EntityNotFoundException("Subject not found for $assessmentUuid ")
  }
}
