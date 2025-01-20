package uk.gov.justice.digital.assessments.api.groups

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.api.answers.AnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import uk.gov.justice.digital.assessments.services.QuestionDependencies
import java.util.UUID

data class GroupQuestionDto(
  @Schema(description = "Reference Question UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val questionId: UUID? = null,

  @Schema(description = "Reference Question Code", example = "RSR_23")
  val questionCode: String? = null,

  @Schema(description = "Answer Type", example = "to-do")
  val answerType: String? = null,

  @Schema(description = "Reference Question Text", example = "Some question text")
  val questionText: String? = null,

  @Schema(description = "Reference Question Help Text", example = "Some question help text")
  val helpText: String? = null,

  @Schema(description = "Group or Question should be displayed read only", example = "false")
  val readOnly: Boolean? = null,

  @Schema(description = "Is the question display conditional on some other question", example = "false")
  val conditional: Boolean? = null,

  @Schema(description = "Reference Data Category", example = "GENDER")
  val referenceDataCategory: String? = null,

  @Schema(description = "Reference Answers")
  val answerDtos: Collection<AnswerDto>? = null,
) : GroupContentDto {
  companion object {
    fun from(
      questionEntity: QuestionEntity,
      questionGroupEntity: QuestionGroupEntity,
      questionDependencies: QuestionDependencies,
    ): GroupQuestionDto = GroupQuestionDto(
      questionId = questionEntity.questionUuid,
      questionCode = questionEntity.questionCode,
      answerType = questionEntity.answerType,
      questionText = questionEntity.questionText,
      helpText = questionEntity.questionHelpText,
      readOnly = questionGroupEntity.readOnly,
      conditional = questionDependencies.hasDependency(questionEntity.questionUuid),
      referenceDataCategory = questionEntity.referenceDataCategory,
      answerDtos = AnswerDto.from(
        questionEntity.answerEntities,
        questionDependencies.answerTriggers(questionEntity.questionUuid),
      ),
    )
  }
}
