package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.services.AnswerDependencies
import uk.gov.justice.digital.assessments.services.QuestionDependencies
import java.util.*

data class AnswerSchemaDto (
        @Schema(description = "Answer Schema UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
        val answerSchemaUuid: UUID,

        @Schema(description = "Answer Schema Code", example = "RSR-01-a")
        val answerSchemaCode: String? = null,

        @Schema(description = "Answer Value", example = "Some answer value")
        val value: String? = null,

        @Schema(description = "Answer Text", example = "Some answer text")
        val text: String? = null,

        @Schema(description = "Does setting the question to this value trigger the display of another question?", example = "<UUID>")
        val conditional: UUID? = null

) {
    companion object{

        fun from(
                answerSchemaEntities: Collection<AnswerSchemaEntity>?,
                answerDependencies: AnswerDependencies = { null }
        ): Set<AnswerSchemaDto>{
            if (answerSchemaEntities.isNullOrEmpty()) return emptySet()
            return answerSchemaEntities.map { from(it, answerDependencies) }.toSet()
        }

        fun from(
                answerSchemaEntity: AnswerSchemaEntity,
                answerDependencies: AnswerDependencies
        ): AnswerSchemaDto{
            return AnswerSchemaDto(
                    answerSchemaEntity.answerSchemaUuid,
                    answerSchemaEntity.answerSchemaCode,
                    answerSchemaEntity.value,
                    answerSchemaEntity.text,
                    answerDependencies(answerSchemaEntity.value)
            )
        }
    }
}