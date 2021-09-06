package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentSchemaCode

data class CreateAssessmentEpisodeDto(
  @Schema(description = "The reason triggering the creation of a new episode", required = true)
  val changeReason: String,

  @Schema(description = "Assessment Schema Code", example = "ROSH")
  val assessmentSchemaCode: AssessmentSchemaCode
)
