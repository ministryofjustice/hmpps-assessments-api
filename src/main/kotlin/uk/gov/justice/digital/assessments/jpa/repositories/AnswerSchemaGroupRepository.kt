package uk.gov.justice.digital.assessments.jpa.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.util.*

@Repository
interface AnswerSchemaGroupRepository:JpaRepository<AnswerSchemaGroupEntity, String>{

    fun findByAnswerSchemaGroupUuid(answerSchemaGroupUuid: UUID):AnswerSchemaGroupEntity?
}