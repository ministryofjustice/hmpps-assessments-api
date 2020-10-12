package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.GroupSummaryEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.util.*

@Repository
interface QuestionGroupRepository: JpaRepository<QuestionGroupEntity, String> {

    fun findByGroupGroupUuid(groupId: UUID): Collection<QuestionGroupEntity>?

    @Query(value = "select (g.heading\\:\\:varchar) as heading " +
                   "from grouping g ",
           nativeQuery = true)
    fun listGroups(): Collection<GroupSummaryEntity>
}