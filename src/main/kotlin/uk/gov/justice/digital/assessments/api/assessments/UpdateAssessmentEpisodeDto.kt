package uk.gov.justice.digital.assessments.api.assessments

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.api.answers.AnswersDto

class UpdateAssessmentEpisodeDto(
  @Schema(description = "Answers associated with this episode")
  val answers: AnswersDto = mapOf(),
)
