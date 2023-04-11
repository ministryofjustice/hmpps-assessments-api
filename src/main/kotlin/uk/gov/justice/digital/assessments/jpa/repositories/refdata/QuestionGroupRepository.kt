package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupSummaryEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import java.util.UUID

@Repository
interface QuestionGroupRepository : JpaRepository<QuestionGroupEntity, String> {

  fun findByGroupGroupUuid(groupId: UUID): Collection<QuestionGroupEntity>?

  @Query(
    value = "select (g.group_uuid\\:\\:varchar) as groupUuid, " +
      "(g.group_code\\:\\:varchar) as groupCode," +
      "(g.heading\\:\\:varchar) as heading, " +
      "count(qg.content_uuid) as contentCount, " +
      "count(case when qg.content_type = 'grouping' then qg.content_uuid end) as groupCount, " +
      "count(case when qg.content_type = 'question' then qg.content_uuid end) as questionCount " +
      "from hmppsassessmentsschemas.question_group qg " +
      "left join hmppsassessmentsschemas.grouping g on qg.group_uuid = g.group_uuid " +
      "group by g.group_uuid, g.heading, g.group_code",
    nativeQuery = true,
  )
  fun listGroups(): Collection<GroupSummaryEntity>
}
