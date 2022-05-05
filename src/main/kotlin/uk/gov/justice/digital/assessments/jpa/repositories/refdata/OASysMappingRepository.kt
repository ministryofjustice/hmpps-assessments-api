package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.OASysMappingEntity
import java.util.UUID

@Repository
interface OASysMappingRepository : JpaRepository<OASysMappingEntity, String> {
  @Suppress("FunctionName")
  fun findAllByQuestion_QuestionUuidIn(questionUuids: Collection<UUID>): Collection<OASysMappingEntity>?
  @Suppress("FunctionName")
  fun findAllByQuestion_QuestionCodeIn(questionCodes: Collection<String>): Collection<OASysMappingEntity>?
  fun findAllBySectionCodeIn(sectionCodes: Collection<String>): Collection<OASysMappingEntity>
}
