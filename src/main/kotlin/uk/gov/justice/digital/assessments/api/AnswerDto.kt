package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import java.util.UUID

data class AnswerDto(
  @Schema(description = "Answer values", example = "Some random text")
  val answer: Collection<String> = emptyList()
) {
  companion object {
    fun from(answers: MutableMap<UUID, AnswerEntity>?): Map<UUID, AnswerDto>? {
      return answers?.mapValues { AnswerDto(it.value.answers) }
    }
  }
}
