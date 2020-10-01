package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.util.*

data class GetQuestionsForGroupDto(

        @Schema(description = "Reference Question Schema ID", example = "1234")
        val questionSchemaId: Long?,

        @Schema(description = "Reference Question Schema UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
        val questionSchemaUuid: UUID?,

        @Schema(description = "Reference Question Schema Code", example = "RSR_23")
        val questionCode: String? = null,

        @Schema(description = "OASys Question Code", example = "OGR_01")
        val oasysQuestionCode: String? = null,

        @Schema(description = "Reference Answer Schemas")
        val answerSchemas: List<AnswerSchemaDto>? = null,

        @Schema(description = "Answer Type", example = "to-do")
        val answerType: String? = null,

        @Schema(description = "Reference Question Text", example = "Some question text")
        val questionText: String? = null,

        @Schema(description = "Reference Question Help Text", example = "Some question help text")
        val questionHelpText: String? = null,

        @Schema(description = "Question Order for Group", example = "1")
        val displayOrder : String? = null,

        @Schema(description = "Question Mandatory status for Group", example = "mandatory")
        val mandatory : String? = null,

        @Schema(description = "Question Validation for Group", example = "to-do")
        val validation : String? = null,
){
    companion object{

        fun from(questionSchemaEntity: QuestionSchemaEntity, questionGroupEntity: QuestionGroupEntity): GetQuestionsForGroupDto{
            return GetQuestionsForGroupDto(
                    questionSchemaId = questionSchemaEntity.questionSchemaId,
                    questionSchemaUuid = questionSchemaEntity.questionSchemaUuid,
                    questionCode = questionSchemaEntity.questionCode,
                    oasysQuestionCode = questionSchemaEntity.oasysQuestionCode,
                    answerSchemas = AnswerSchemaDto.from(questionSchemaEntity.answerSchemaEntities),
                    answerType = questionSchemaEntity.answerType,
                    questionText = questionSchemaEntity.questionText,
                    questionHelpText = questionSchemaEntity.questionHelpText,
                    displayOrder = questionGroupEntity.displayOrder,
                    mandatory = questionGroupEntity.mandatory,
                    validation = questionGroupEntity.validation
            ) }
    }
}