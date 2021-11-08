package uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class RiskSummary(
  val overallRiskLevel: String,
  val riskInCommunity: RiskInCommunityDto,
  val assessedOn: LocalDate,
)

class RiskInCommunityDto(
  @JsonProperty("VERY_HIGH")
  val veryHigh: Collection<String> = emptyList(),
  @JsonProperty("HIGH")
  val high: Collection<String> = emptyList(),
  @JsonProperty("MEDIUM")
  val medium: Collection<String> = emptyList(),
  @JsonProperty("LOW")
  val low: Collection<String> = emptyList(),
)
