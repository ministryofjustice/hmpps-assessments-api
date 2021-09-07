package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema

class UpdateAssessmentEpisodeDto(
  @Schema(description = "Answers associated with this episode")
  val answers: Answers = mapOf()
)
