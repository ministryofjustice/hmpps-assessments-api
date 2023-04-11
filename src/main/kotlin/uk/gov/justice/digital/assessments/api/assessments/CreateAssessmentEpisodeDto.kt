package uk.gov.justice.digital.assessments.api.assessments

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

data class CreateAssessmentEpisodeDto(
  @Schema(description = "The reason triggering the creation of a new episode", required = true)
  val changeReason: String,

  @Schema(description = "The Delius Event ID", required = true)
  val eventID: Long,

  @Schema(description = "Assessment Schema Code", example = "ROSH")
  val assessmentType: AssessmentType,

)
