package uk.gov.justice.digital.assessments.api.assessments

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

data class CreateAssessmentDto(
  @Schema(description = "Delius Event ID", example = "1234")
  val deliusEventId: Long? = null,

  @Schema(description = "Delius Event Type", example = "EVENT_ID", required = false)
  val deliusEventType: DeliusEventType = DeliusEventType.EVENT_INDEX,

  @Schema(description = "Offender CRN", example = "CRN1")
  val crn: String? = null,

  @Schema(description = "Assessment Type", example = "ROSH")
  val assessmentSchemaCode: AssessmentType

) {
  fun isDelius() = (crn != null && deliusEventId != null)
}
