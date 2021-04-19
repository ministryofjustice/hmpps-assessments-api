package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.OASysMappingEntity
import java.util.*

@Repository
interface OASysMappingRepository : JpaRepository<OASysMappingEntity, String> {
  @Suppress("FunctionName")
  fun findAllByQuestionSchema_QuestionSchemaUuidIn(questionUuids: Collection<UUID>): Collection<OASysMappingEntity>?
}
