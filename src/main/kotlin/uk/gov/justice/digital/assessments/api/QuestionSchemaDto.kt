package uk.gov.justice.digital.assessments.api

import io.swagger.annotations.ApiModelProperty
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import java.time.LocalDateTime
import java.util.*

data class QuestionSchemaDto(

        @ApiModelProperty(value = "Question Schema primary key", example = "1234")
        val questionSchemaId: Long?,

        @ApiModelProperty(value = "Question Schema UUID", example = "")
        val questionSchemaUuid: UUID?,

        @ApiModelProperty(value = "Question Code", example = "RSR_23")
        val questionCode: String? = null,

        @ApiModelProperty(value = "OASys Question Code", example = "RSR_23")
        val oasysQuestionCode: String? = null,

        @ApiModelProperty(value = "Question Start Date", example = "2020-01-02T16:00:00")
        val questionStart: LocalDateTime? = null,

        @ApiModelProperty(value = "Question End Date", example = "2020-01-02T16:00:00")
        val questionEnd: LocalDateTime? = null,

        @ApiModelProperty(value = "Answer Type", example = "FreeText")
        val answerType: String? = null,

        @ApiModelProperty(value = "Question Text", example = "")
        val questionText: String? = null,

        @ApiModelProperty(value = "Question Help Text", example = "")
        val questionHelpText: String? = null,

        @ApiModelProperty(value = "List of Reference Answers", example = "")
        val answerSchemas: Collection<AnswerSchemaDto>,

        ) {

    companion object {

        fun from(questionSchemas: Collection<QuestionSchemaEntity>?): List<QuestionSchemaDto> {
            return questionSchemas?.map { from(it) }?.toList().orEmpty()
        }

        fun from(questionSchema: QuestionSchemaEntity?): QuestionSchemaDto {
            return QuestionSchemaDto(
                    questionSchema?.questionSchemaId,
                    questionSchema?.questionSchemaUuid,
                    questionSchema?.questionCode,
                    questionSchema?.oasysQuestionCode,
                    questionSchema?.questionStartDate,
                    questionSchema?.questionEndDate,
                    questionSchema?.answerType,
                    questionSchema?.questionText,
                    questionSchema?.questionHelpText,
                    AnswerSchemaDto.from(questionSchema?.answerSchemaEntities)
            )
        }
    }
}