package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.RequestData

@Service
class OffenderService(
  private val communityApiRestClient: CommunityApiRestClient,
) {

  fun getOffence(
    eventType: DeliusEventType?,
    crn: String,
    eventId: Long
  ): OffenceDto {
    log.info("Entered getOffence crn: $crn")
    return if (eventType == DeliusEventType.EVENT_ID) {
      getOffenceFromConvictionId(crn, eventId)
    } else {
      getOffenceFromConvictionIndex(crn, eventId)
    }
  }

  fun getOffender(crn: String): OffenderDto {
    log.info("Requesting offender details for crn: $crn")
    val communityOffenderDto = communityApiRestClient.getOffender(crn)
      ?: throw EntityNotFoundException("No offender found for crn: $crn")
    return OffenderDto.from(communityOffenderDto)
  }

  fun getOffenceFromConvictionIndex(crn: String, eventId: Long): OffenceDto {
    log.info("Requesting offences for crn: $crn")
    val convictions = communityApiRestClient.getConvictions(crn)
      ?: throw EntityNotFoundException("Could not get convictions for crn: $crn")
    val conviction = convictions.find { it.index == eventId }
      ?: throw EntityNotFoundException("Could not get conviction for crn: $crn, event ID: $eventId")
    return OffenceDto.from(conviction)
  }

  fun getOffenceFromConvictionId(crn: String, convictionId: Long): OffenceDto {
    log.info("Requesting offences for crn: $crn")
    val conviction = communityApiRestClient.getConviction(crn, convictionId)
      ?: throw EntityNotFoundException("Could not get convictions for crn: $crn")
    return OffenceDto.from(conviction)
  }

  fun validateUserAccess(crn: String) {
    communityApiRestClient.verifyUserAccess(crn, RequestData.getUserName())
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
