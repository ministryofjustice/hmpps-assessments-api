package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AssessmentSchemaEntity

@Repository
interface AssessmentSchemaRepository : JpaRepository<AssessmentSchemaEntity, Long> {

  fun findByAssessmentSchemaCode(assessmentSchemaCode: AssessmentSchemaCode): AssessmentSchemaEntity?
}
