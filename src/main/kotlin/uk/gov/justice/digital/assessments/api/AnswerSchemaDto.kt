package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import java.time.LocalDateTime
import java.util.*

data class AnswerSchemaDto (

        @Schema(description = "Answer Schema primary key", example = "1234")
        val answerSchemaId: Long,

        @Schema(description = "Answer Schema UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
        val answerSchemaUuid: UUID,

        @Schema(description = "Answer Schema Code", example = "RSR-01-a")
        val answerSchemaCode: String? = null,

        @Schema(description = "Question Schema UUID")
        val questionSchema: UUID? = null,

        @Schema(description = "Answer Start Date", example = "2020-01-02T16:00:00")
        val answerStart: LocalDateTime? = null,

        @Schema(description = "Answer End Date", example = "2020-01-02T16:00:00")
        val answerEnd: LocalDateTime? = null,

        @Schema(description = "Answer Value", example = "Some answer value")
        val value: String? = null,

        @Schema(description = "Answer Text", example = "Some answer text")
        val text: String? = null

) {
    companion object{

        fun from(answerSchemaEntities: Collection<AnswerSchemaEntity>?): List<AnswerSchemaDto>{
            if (answerSchemaEntities.isNullOrEmpty()) return emptyList()
            return answerSchemaEntities.map { from(it) }.toList()
        }

        fun from(answerSchemaEntity: AnswerSchemaEntity): AnswerSchemaDto{
            return AnswerSchemaDto(
                    answerSchemaEntity.answerSchemaId,
                    answerSchemaEntity.answerSchemaUuid,
                    answerSchemaEntity.answerSchemaCode,
                    answerSchemaEntity.answerSchemaGroup?.answerSchemaGroupUuid,
                    answerSchemaEntity.answerStart,
                    answerSchemaEntity.answerEnd,
                    answerSchemaEntity.value,
                    answerSchemaEntity.text
            )
        }
    }
}