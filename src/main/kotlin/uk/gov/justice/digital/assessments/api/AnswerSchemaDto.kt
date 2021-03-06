package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AnswerSchemaEntity
import uk.gov.justice.digital.assessments.services.AnswerDependencies
import java.util.UUID

data class AnswerSchemaDto(
  @Schema(description = "Answer Schema UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val answerSchemaUuid: UUID,

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
      answerSchemaEntities: Collection<AnswerSchemaEntity>?,
      answerDependencies: AnswerDependencies = { null }
    ): Collection<AnswerSchemaDto> {
      if (answerSchemaEntities.isNullOrEmpty()) return emptySet()
      return answerSchemaEntities.map {
        from(it, answerDependencies)
      }.toSet()
    }

    fun from(
      answerSchemaEntity: AnswerSchemaEntity,
      answerDependencies: AnswerDependencies
    ): AnswerSchemaDto {
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
