package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import java.util.UUID

@Repository
interface QuestionRepository : JpaRepository<QuestionEntity, String> {
  fun findByQuestionUuid(questionUuid: UUID): QuestionEntity?
}
