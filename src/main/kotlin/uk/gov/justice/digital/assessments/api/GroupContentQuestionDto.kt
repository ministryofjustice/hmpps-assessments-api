package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.util.*

data class GroupContentQuestionDto(
        @Schema(description = "Reference Question Schema UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
        val questionId: UUID?,

        @Schema(description = "Reference Question Schema Code", example = "RSR_23")
        val questionCode: String? = null,

        @Schema(description = "Answer Type", example = "to-do")
        val answerType: String? = null,

        @Schema(description = "Reference Question Text", example = "Some question text")
        val questionText: String? = null,

        @Schema(description = "Reference Question Help Text", example = "Some question help text")
        val helpText: String? = null,

        @Schema(description = "Question Order for Group", example = "1")
        val displayOrder : String? = null,

        @Schema(description = "Question Mandatory status for Group", example = "mandatory")
        val mandatory : String? = null,

        @Schema(description = "Question Validation for Group", example = "to-do")
        val validation : String? = null,

        @Schema(description = "Reference Answer Schemas")
        val answerSchemas: List<AnswerSchemaDto>? = null,
): GroupContentDto {
    val type = "question"

    companion object{
        fun from(questionSchemaEntity: QuestionSchemaEntity, questionGroupEntity: QuestionGroupEntity): GroupContentQuestionDto{
            return GroupContentQuestionDto(
                    questionId = questionSchemaEntity.questionSchemaUuid,
                    questionCode = questionSchemaEntity.questionCode,
                    answerType = questionSchemaEntity.answerType,
                    questionText = questionSchemaEntity.questionText,
                    helpText = questionSchemaEntity.questionHelpText,
                    displayOrder = questionGroupEntity.displayOrder,
                    mandatory = questionGroupEntity.mandatory,
                    validation = questionGroupEntity.validation,
                    answerSchemas = AnswerSchemaDto.from(questionSchemaEntity.answerSchemaEntities)
            ) }
    }
}