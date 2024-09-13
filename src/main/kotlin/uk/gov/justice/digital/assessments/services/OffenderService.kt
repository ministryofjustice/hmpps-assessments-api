package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.RequestData

@Service
class OffenderService(
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient,
) {

  fun getOffender(crn: String, eventId: Long): OffenderDto {
    log.info("Requesting offender details for crn: $crn")
    return OffenderDto.from(getDeliusCaseDetails(crn, eventId), eventId)
  }

  fun getDeliusCaseDetails(crn: String, eventId: Long): CaseDetails {
    log.info("Entered getCommunityOffender with crn: $crn")
    return deliusIntegrationRestClient.getCaseDetails(crn, eventId)
      ?: throw EntityNotFoundException("No Case Details found for crn: $crn, eventId: $eventId")
  }

  fun validateUserAccess(crn: String) {
    deliusIntegrationRestClient.verifyUserAccess(crn, RequestData.getUserName())
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
