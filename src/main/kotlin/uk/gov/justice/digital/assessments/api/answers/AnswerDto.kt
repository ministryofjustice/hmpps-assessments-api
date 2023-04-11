package uk.gov.justice.digital.assessments.api.answers

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.api.ConditionalsSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerEntity
import uk.gov.justice.digital.assessments.services.AnswerDependencies
import java.util.UUID

data class AnswerDto(
  @Schema(description = "Answer UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val answerUuid: UUID,

  @Schema(description = "Answer Schema Code", example = "RSR-01-a")
  val answerSchemaCode: String? = null,

  @Schema(description = "Answer Value", example = "Some answer value")
  val value: String? = null,

  @Schema(description = "Answer Text", example = "Some answer text")
  val text: String? = null,

  @Schema(description = "List of questions to display when this answer is selected, and whether to display inline")
  val conditionals: Collection<ConditionalsSchemaDto>? = null,
) {
  companion object {
    fun from(
      answerEntities: Collection<AnswerEntity>?,
      answerDependencies: AnswerDependencies = { null },
    ): Collection<AnswerDto> {
      if (answerEntities.isNullOrEmpty()) return emptySet()
      return answerEntities.map {
        from(it, answerDependencies)
      }.toSet()
    }

    fun from(
      answerEntity: AnswerEntity,
      answerDependencies: AnswerDependencies,
    ): AnswerDto {
      return AnswerDto(
        answerEntity.answerUuid,
        answerEntity.answerCode,
        answerEntity.value,
        answerEntity.text,
        answerDependencies(answerEntity.value),
      )
    }
  }
}
