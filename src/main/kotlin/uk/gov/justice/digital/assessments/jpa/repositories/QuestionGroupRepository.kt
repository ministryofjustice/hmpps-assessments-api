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

    @Query(value = "select (g.group_uuid\\:\\:varchar) as groupUuid, " +
                   "(g.heading\\:\\:varchar) as heading, " +
                   "count(qg.content_uuid) as contentCount, " +
                   "count(case when qg.content_type = 'grouping' then qg.content_uuid end) as groupCount, " +
                   "count(case when qg.content_type = 'question' then qg.content_uuid end) as questionCount, " +
                   "from question_group qg " +
                   "left join grouping g on qg.group_uuid = g.group_uuid " +
                   "group by g.group_uuid, g.heading ",
           nativeQuery = true)
    fun listGroups(): Collection<GroupSummaryEntity>
}