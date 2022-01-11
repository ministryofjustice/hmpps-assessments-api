package uk.gov.justice.digital.assessments.jpa.repositories.refdata

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import java.util.UUID
import javax.persistence.QueryHint

@Repository
interface QuestionSchemaRepository : JpaRepository<QuestionSchemaEntity, String> {
  fun findByQuestionSchemaUuid(questionSchemaId: UUID): QuestionSchemaEntity?
}
