package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.util.UUID

@Repository
interface QuestionSchemaRepository : JpaRepository<QuestionSchemaEntity, String> {

  fun findByQuestionSchemaUuid(questionSchemaId: UUID): QuestionSchemaEntity?
}
