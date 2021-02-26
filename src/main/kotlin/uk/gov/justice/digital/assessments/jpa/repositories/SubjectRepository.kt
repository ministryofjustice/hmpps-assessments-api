package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity

@Repository
interface SubjectRepository : JpaRepository<SubjectEntity, Long> {
  fun findBySourceAndSourceId(source: String, sourceId: String): SubjectEntity?

  fun findAllByCrnAndSourceOrderByCreatedDateDesc(crn: String, source: String): List<SubjectEntity>
}
