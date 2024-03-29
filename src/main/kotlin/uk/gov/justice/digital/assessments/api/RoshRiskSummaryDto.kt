package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class RoshRiskSummaryDto(
  @Schema(description = "Overall ROSH risk score", example = "HIGH")
  val overallRisk: String? = null,

  @Schema(description = "Assessed on", example = "2021-10-10")
  val assessedOn: LocalDate? = null,

  @Schema(description = "Risk in the community", example = "HIGH")
  val riskInCommunity: Map<String, String?> = hashMapOf(),
)
