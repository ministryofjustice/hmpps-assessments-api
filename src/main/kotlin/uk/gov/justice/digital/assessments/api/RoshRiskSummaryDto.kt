package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class RoshRiskSummaryDto(
  @Schema(description = "Has a RoSH risk assessment been completed?", example = "true")
  val hasBeenCompleted: Boolean? = null,

  @Schema(description = "Overall ROSH risk score", example = "HIGH")
  val overallRisk: String? = null,

  @Schema(description = "Assessed on", example = "2021-10-10")
  val lastUpdated: LocalDate? = null,

  @Schema(description = "Risk to children in the community", example = "HIGH")
  val riskToChildrenInCommunity: String? = null,

  @Schema(description = "Risk to public in the community", example = "HIGH")
  val riskToPublicInCommunity: String? = null,

  @Schema(description = "Risk to known adult in the community", example = "HIGH")
  val riskToKnownAdultInCommunity: String? = null,

  @Schema(description = "Risk to staff in the community", example = "HIGH")
  val riskToStaffInCommunity: String? = null,
)
