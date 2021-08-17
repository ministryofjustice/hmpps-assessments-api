package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

class UpdateAssessmentEpisodeDto(
  @Schema(description = "Answers associated with this episode")
  val answers: Map<String, Collection<String>> = emptyMap()
) {

  fun asAnswersDtos(): Map<String, AnswersDto> {
    return answers.mapValues { asAnswersDto(it.value) }
  }

  companion object {
    private fun asAnswersDto(ans: Collection<String>): AnswersDto {
      val filteredAnswers = ans.filter { it.isNotBlank() }
      return AnswersDto(listOf(AnswerDto(filteredAnswers)))
    }
  }
}
