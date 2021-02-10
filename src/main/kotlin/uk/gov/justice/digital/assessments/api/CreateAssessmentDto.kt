package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

data class CreateAssessmentDto(
  @Schema(description = "Supervision ID", example = "CRN1")
  val supervisionId: String? = null,

  @Schema(description = "Court Code", example = "SHF")
  val courtCode: String? = null,

  @Schema(description = "Case Number", example = "1234567890")
  val caseNumber: String? = null,

  @Schema(description = "Assessment Type", example = "SHORT_FORM_PSR")
  val assessmentType: AssessmentType = AssessmentType.SHORT_FORM_PSR

) {
  fun isSupervision() = supervisionId != null
  fun isCourtCase() = (courtCode != null && caseNumber != null)
}
