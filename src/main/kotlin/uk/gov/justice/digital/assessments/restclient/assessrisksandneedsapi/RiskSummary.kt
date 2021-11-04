package uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi

import com.fasterxml.jackson.annotation.JsonProperty

data class RiskSummary(
  val overallRiskLevel: String,
  val riskInCommunity: RiskInCommunityDto,
)

class RiskInCommunityDto(
  @JsonProperty("HIGH")
  val high: Collection<String> = emptyList(),
  @JsonProperty("MEDIUM")
  val medium: Collection<String> = emptyList(),
  @JsonProperty("LOW")
  val low: Collection<String> = emptyList(),
)
