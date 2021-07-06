package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaEntity
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import java.util.UUID

@Repository
interface AssessmentSchemaRepository : JpaRepository<AssessmentSchemaEntity, Long> {

  fun findByOasysAssessmentType(oasysAssessmentType: OasysAssessmentType): AssessmentSchemaEntity?
}
