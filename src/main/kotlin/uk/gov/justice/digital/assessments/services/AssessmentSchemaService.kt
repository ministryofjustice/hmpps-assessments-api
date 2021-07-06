package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.AssessmentSchemaRepository
import uk.gov.justice.digital.assessments.services.exceptions.OasysAssessmentTypeMappingMissing

@Service
class AssessmentSchemaService(private val assessmentSchemaRepository: AssessmentSchemaRepository) {

  fun toOasysAssessmentType(assessmentSchemaCode: AssessmentSchemaCode?): OasysAssessmentType {
    return assessmentSchemaRepository.findByAssessmentSchemaCode(assessmentSchemaCode!!)?.oasysAssessmentType
      ?: throw OasysAssessmentTypeMappingMissing("Corresponding Oasys assessment type mapping not found for :$assessmentSchemaCode")
  }
}
