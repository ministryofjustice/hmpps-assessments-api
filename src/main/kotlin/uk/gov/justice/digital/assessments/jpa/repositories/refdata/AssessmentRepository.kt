package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AssessmentEntity

@Repository(value = "assessmentReferenceDataRepository")
interface AssessmentRepository : JpaRepository<AssessmentEntity, Long> {

  fun findByAssessmentSchemaCode(assessmentSchemaCode: AssessmentSchemaCode): AssessmentEntity?
}
