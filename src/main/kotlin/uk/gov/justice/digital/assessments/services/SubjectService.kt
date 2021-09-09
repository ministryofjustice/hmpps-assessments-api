package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.util.UUID

@Service
class SubjectService(
  private val assessmentRepository: AssessmentRepository,
  private val subjectRepository: SubjectRepository
) {

  fun getSubjectForAssessment(assessmentUuid: UUID): SubjectEntity {
    val assessmentEntity = (
      assessmentRepository.findByAssessmentUuid(assessmentUuid)
        ?: throw EntityNotFoundException("Assessment $assessmentUuid not found")
      )

    return assessmentEntity.subject ?: throw EntityNotFoundException("Subject not found for $assessmentUuid ")
  }

  fun getLatestEpisodeOfTypeForSubjectWithCrn(
    assessmentSchemaCode: AssessmentSchemaCode,
    crn: String
  ): AssessmentEpisodeDto {
    val subjectEntity = (
      subjectRepository.findByCrn(crn)
        ?: throw EntityNotFoundException("Subject for crn $crn not found")
      )
    val latestClosedEpisodeOfType = subjectEntity.assessment?.getLatestClosedEpisodeOfType(assessmentSchemaCode)
      ?: throw EntityNotFoundException("Closed Episode for Subject for crn $crn not found for type $assessmentSchemaCode ")

    return AssessmentEpisodeDto.from(latestClosedEpisodeOfType)
  }
}
