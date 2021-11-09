package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode

data class CreateAssessmentDto(
  @Schema(description = "Delius Event ID", example = "1234")
  val deliusEventId: Long? = null,

  @Schema(description = "Delius Event Type", example = "EVENT_ID", required = false)
  val deliusEventType: DeliusEventType = DeliusEventType.EVENT_INDEX,

  @Schema(description = "Offender CRN", example = "CRN1")
  val crn: String? = null,

  @Schema(description = "Court Code", example = "SHF")
  val courtCode: String? = null,

  @Schema(description = "Case Number", example = "1234567890")
  val caseNumber: String? = null,

  @Schema(description = "Assessment Schema Code", example = "ROSH")
  val assessmentSchemaCode: AssessmentSchemaCode

) {
  fun isCourtCase() = (courtCode != null && caseNumber != null)
  fun isDelius() = (crn != null && deliusEventId != null)
}
