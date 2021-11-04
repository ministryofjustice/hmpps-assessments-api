package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.RegistrationsDto
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient

@Service
class RisksService(
  private val communityApiRestClient: CommunityApiRestClient,
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getRegistrationsForAssessment(crn: String): RegistrationsDto {
    log.info("Getting registrations for crn: $crn")
    val deliusRegistrations = communityApiRestClient.getRegistrations(crn)
      ?: throw Exception("Failed to get registrations for crn $crn")

    return RegistrationsDto.from(deliusRegistrations.registrations.toList())
  }

  fun getRoshRiskSummaryForAssessment(crn: String): RoshRiskSummaryDto {
    log.info("Getting ROSH risk summary for crn: $crn")

    val riskSummary = assessRisksAndNeedsApiRestClient.getRoshRiskSummary(crn)
      ?: throw Exception("Failed to get ROSH risk summary for $crn")

    return RoshRiskSummaryDto.from(riskSummary)
  }
}
