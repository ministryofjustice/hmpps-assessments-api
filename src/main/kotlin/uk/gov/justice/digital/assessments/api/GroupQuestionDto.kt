package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionSchemaEntity
import uk.gov.justice.digital.assessments.services.QuestionDependencies
import java.util.UUID

data class GroupQuestionDto(
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
  val displayOrder: Int? = 0,

  @Schema(description = "Question Mandatory status for Group", example = "true")
  val mandatory: Boolean? = null,

  @Schema(description = "Question Validation for Group", example = "to-do")
  val validation: String? = null,

  @Schema(description = "Is the question display conditional on some other question", example = "false")
  val conditional: Boolean? = null,

  @Schema(description = "Reference Answer Schemas")
  val answerSchemas: Set<AnswerSchemaDto>? = null,
) : GroupContentDto {
  companion object {
    fun from(
      questionSchemaEntity: QuestionSchemaEntity,
      questionGroupEntity: QuestionGroupEntity,
      questionDependencies: QuestionDependencies
    ): GroupQuestionDto {
      return GroupQuestionDto(
        questionId = questionSchemaEntity.questionSchemaUuid,
        questionCode = questionSchemaEntity.questionCode,
        answerType = questionSchemaEntity.answerType,
        questionText = questionSchemaEntity.questionText,
        helpText = questionSchemaEntity.questionHelpText,
        displayOrder = questionGroupEntity.displayOrder,
        mandatory = questionGroupEntity.mandatory,
        validation = questionGroupEntity.validation,
        conditional = questionDependencies.hasDependency(questionSchemaEntity.questionSchemaUuid),
        answerSchemas = AnswerSchemaDto.from(
          questionSchemaEntity.answerSchemaEntities,
          questionDependencies.answerTriggers(questionSchemaEntity.questionSchemaUuid),
          questionDependencies.displayInline(questionSchemaEntity.questionSchemaUuid)
        )
      )
    }
  }
}

