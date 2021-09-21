package uk.gov.justice.digital.assessments.jpa.repositories.assessments

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity

@Repository
interface SubjectRepository : JpaRepository<SubjectEntity, Long> {

  fun findByCrn(crn: String): SubjectEntity?
}
