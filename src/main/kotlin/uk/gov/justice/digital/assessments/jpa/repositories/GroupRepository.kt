package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import java.util.*

@Repository
interface GroupRepository: JpaRepository<GroupEntity, String> {
    fun findByGroupUuid(groupId: UUID): GroupEntity?
}

