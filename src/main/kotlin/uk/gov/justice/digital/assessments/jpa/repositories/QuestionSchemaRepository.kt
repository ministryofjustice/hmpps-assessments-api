package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity

@Repository
interface QuestionSchemaRepository:JpaRepository<QuestionSchemaEntity, String>{

    fun findByQuestionSchemaId(questionSchemaId: Long):QuestionSchemaEntity?
}