package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.RegistrationsDto
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient

@Service
class RisksService(
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient,
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getRegistrationsForAssessment(crn: String, eventId: Long): RegistrationsDto {
    log.info("Getting registrations for crn: $crn")
    val caseDetails = deliusIntegrationRestClient.getCaseDetails(crn, eventId)
      ?: throw Exception("Failed to get registrations for crn $crn")

    return RegistrationsDto.from(caseDetails)
  }

  fun getRoshRiskSummaryForAssessment(crn: String): RoshRiskSummaryDto {
    log.info("Getting ROSH risk summary for crn: $crn")

    return assessRisksAndNeedsApiRestClient.getRoshRiskSummary(crn)
      ?: throw Exception("Failed to get ROSH risk summary for $crn")
  }
}
