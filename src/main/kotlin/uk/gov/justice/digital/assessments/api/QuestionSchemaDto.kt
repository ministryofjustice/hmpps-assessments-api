package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import java.time.LocalDateTime
import java.util.UUID

data class QuestionSchemaDto(

  @Schema(description = "Question Schema primary key", example = "1234")
  val questionSchemaId: Long?,

  @Schema(description = "Question Schema UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val questionSchemaUuid: UUID?,

  @Schema(description = "Question Code", example = "RSR_23")
  val questionCode: String? = null,

  @Schema(description = "Question Start Date", example = "2020-01-02T16:00:00")
  val questionStart: LocalDateTime? = null,

  @Schema(description = "Question End Date", example = "2020-01-02T16:00:00")
  val questionEnd: LocalDateTime? = null,

  @Schema(description = "Answer Type", example = "Free Text")
  val answerType: String? = null,

  @Schema(description = "Question Text", example = "Some question text")
  val questionText: String? = null,

  @Schema(description = "Question Help Text", example = "Some question help text")
  val questionHelpText: String? = null,

  @Schema(description = "Reference Data Category")
  val referenceDataCategory: String? = null,

  @Schema(description = "Reference Data Targets")
  val referenceDataTargets: Collection<ReferenceDataTargetDto> = emptyList(),

  @Schema(description = "List of Reference Answer Schemas")
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
        questionSchema?.questionStartDate,
        questionSchema?.questionEndDate,
        questionSchema?.answerType,
        questionSchema?.questionText,
        questionSchema?.questionHelpText,
        questionSchema?.referenceDataCategory,
        ReferenceDataTargetDto.from(questionSchema?.referenceDataTargets),
        AnswerSchemaDto.from(
          questionSchema?.answerSchemaEntities
        )
      )
    }
  }
}
