package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerGroupEntity
import java.util.UUID

@Repository
interface AnswerGroupRepository : JpaRepository<AnswerGroupEntity, String> {

  fun findByAnswerGroupUuid(answerGroupUuid: UUID): AnswerGroupEntity?
}
