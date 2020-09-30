package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.util.*

@Repository
interface QuestionGroupRepository: JpaRepository<QuestionGroupEntity, String> {

    fun findByGroupGroupUuid(groupId: UUID): Collection<QuestionGroupEntity>?


}