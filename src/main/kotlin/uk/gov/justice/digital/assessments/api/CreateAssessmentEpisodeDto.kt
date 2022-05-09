package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

data class CreateAssessmentEpisodeDto(
  @Schema(description = "The reason triggering the creation of a new episode", required = true)
  val changeReason: String,

  @Schema(description = "The Delius Event ID", required = true)
  val eventID: Long,

  @Schema(description = "Assessment Schema Code", example = "ROSH")
  val assessmentType: AssessmentType,

  @Schema(description = "Delius Event Type", example = "EVENT_ID", required = false)
  val deliusEventType: DeliusEventType = DeliusEventType.EVENT_INDEX
)
