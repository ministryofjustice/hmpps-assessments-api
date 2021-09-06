package uk.gov.justice.digital.assessments.jpa.repositories.assessments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import java.util.UUID

@Repository
interface AssessmentRepository : JpaRepository<AssessmentEntity, Long> {

  fun findByAssessmentUuid(assessmentUuid: UUID): AssessmentEntity?
}
