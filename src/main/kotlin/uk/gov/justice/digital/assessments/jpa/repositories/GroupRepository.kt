package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import java.util.UUID

@Repository
interface GroupRepository : JpaRepository<GroupEntity, String> {
  fun findByGroupUuid(groupId: UUID): GroupEntity?
  fun findByGroupCode(groupCode: String): GroupEntity?
}
