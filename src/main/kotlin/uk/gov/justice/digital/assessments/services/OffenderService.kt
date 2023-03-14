package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.EventTypeNotKnown
import uk.gov.justice.digital.assessments.utils.RequestData

@Service
class OffenderService(
  private val communityApiRestClient: CommunityApiRestClient,
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient
) {

  fun getOffence(
    eventType: DeliusEventType?,
    crn: String,
    eventId: Long
  ): OffenceDto {
    log.info("Entered getOffence crn: $crn")
    return if (eventType == DeliusEventType.EVENT_ID || eventType == null) {
      getOffenceFromConvictionId(crn, eventId)
    } else throw EventTypeNotKnown("Unknown event Type: $eventType")
  }

  fun getOffender(crn: String): OffenderDto {
    log.info("Requesting offender details for crn: $crn")
    return OffenderDto.from(getCommunityOffender(crn))
  }

  fun getCommunityOffender(crn: String): CommunityOffenderDto {
    log.info("Entered getCommunityOffender with crn: $crn")
    return communityApiRestClient.getOffender(crn)
      ?: throw EntityNotFoundException("No offender found for crn: $crn")
  }

  fun getOffenceFromConvictionId(crn: String, convictionId: Long): OffenceDto {
    log.info("Requesting offences for crn: $crn")
    val conviction = deliusIntegrationRestClient.getCaseDetails(crn, convictionId)?.sentence
      ?: throw EntityNotFoundException("Could not get convictions for crn: $crn")
    return OffenceDto.from(conviction, convictionId)
  }

  fun validateUserAccess(crn: String) {
    communityApiRestClient.verifyUserAccess(crn, RequestData.getUserName())
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
