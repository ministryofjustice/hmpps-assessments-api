package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema

data class CreateAssessmentEpisodeDto(
  @Schema(description = "The reason triggering the creation of a new episode", required = true)
  val changeReason: String,

  @Schema(description = "Assessment Type", example = "SHORT_FORMAT_PSR")
  val assessmentType: String? = null
)
