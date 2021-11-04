package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskInCommunityDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskSummary

fun getRiskLevel(riskInCommunityDto: RiskInCommunityDto, risk: String): String {
  return if (riskInCommunityDto.high.contains(risk)) { "HIGH" } else if (riskInCommunityDto.medium.contains(risk)) { "MEDIUM" } else if (riskInCommunityDto.low.contains(risk)) { "LOW" } else { "NOT_KNOWN" }
}

data class RoshRiskSummaryDto(
  @Schema(description = "Overall ROSH risk score", example = "HIGH")
  val overallRisk: String? = null,

  @Schema(description = "Risk to children in the community", example = "HIGH")
  val riskToChildrenInCommunity: String? = null,

  @Schema(description = "Risk to public in the community", example = "HIGH")
  val riskToPublicInCommunity: String? = null,

  @Schema(description = "Risk to known adult in the community", example = "HIGH")
  val riskToKnownAdultInCommunity: String? = null,

  @Schema(description = "Risk to staff in the community", example = "HIGH")
  val riskToStaffInCommunity: String? = null,
) {
  companion object {
    fun from(riskSummary: RiskSummary): RoshRiskSummaryDto {
      return RoshRiskSummaryDto(
        overallRisk = riskSummary.overallRiskLevel,
        riskToChildrenInCommunity = getRiskLevel(riskSummary.riskInCommunity, "Children"),
        riskToPublicInCommunity = getRiskLevel(riskSummary.riskInCommunity, "Public"),
        riskToKnownAdultInCommunity = getRiskLevel(riskSummary.riskInCommunity, "Known adult"),
        riskToStaffInCommunity = getRiskLevel(riskSummary.riskInCommunity, "Staff"),

      )
    }
  }
}
