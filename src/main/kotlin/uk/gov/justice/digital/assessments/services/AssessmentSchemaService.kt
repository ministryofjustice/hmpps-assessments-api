package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.GroupSectionsDto
import uk.gov.justice.digital.assessments.api.GroupWithContentsDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.OasysAssessmentTypeMappingMissing

@Service
class AssessmentSchemaService(
  private val assessmentSchemaRepository: AssessmentSchemaRepository,
  private val questionService: QuestionService
) {

  fun getAssessmentSchema(assessmentSchemaCode: AssessmentSchemaCode?): GroupWithContentsDto {
    val assessmentSchemaGroupUuid =
      assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode!!)?.assessmentSchemaGroup?.group?.groupUuid
        ?: throw EntityNotFoundException("Assessment Schema not found for assessmentSchemaCode $assessmentSchemaCode")

    return questionService.getGroupContents(assessmentSchemaGroupUuid)
  }

  fun getAssessmentSchemaSummary(assessmentSchemaCode: AssessmentSchemaCode?): GroupSectionsDto {
    val assessmentSchemaGroupCode =
      assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode!!)?.assessmentSchemaGroup?.group?.groupCode
        ?: throw EntityNotFoundException("Assessment Schema not found for assessmentSchemaCode $assessmentSchemaCode")

    return questionService.getGroupSections(assessmentSchemaGroupCode)
  }

  fun toOasysAssessmentType(assessmentSchemaCode: AssessmentSchemaCode?): OasysAssessmentType {
    return assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode!!)?.oasysAssessmentType
      ?: throw OasysAssessmentTypeMappingMissing("Corresponding Oasys assessment type mapping not found for :$assessmentSchemaCode")
  }
}
