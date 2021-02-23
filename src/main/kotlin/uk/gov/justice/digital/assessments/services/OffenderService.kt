package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientException
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.api.OffenderDto
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

@Service
class OffenderService(private val communityApiRestClient: CommunityApiRestClient) {

  fun getOffenderAndOffence(crn: String, convictionId: Long): OffenderDto {
    val offender = getOffender(crn)
    val offence = getOffence(crn, convictionId)
    return offender.copy(offence = offence)
  }

  fun getOffender(crn: String): OffenderDto {
    try {
      log.info("Requesting offender details for crn: $crn")
      val communityOffenderDto = communityApiRestClient.getOffender(crn)
        ?: throw EntityNotFoundException("No offender found for crn: $crn")
      return OffenderDto.from(communityOffenderDto)
    } catch (e: WebClientException) {
      println(e.message)
      throw EntityNotFoundException("No offender found for crn: $crn")
    }
  }

  fun getOffence(crn: String, convictionId: Long): OffenceDto {
    try {
      log.info("Requesting main offence details for crn: $crn, conviction id: $convictionId")
      val conviction = communityApiRestClient.getConviction(crn, convictionId)
        ?: throw EntityNotFoundException("No offence found for crn: $crn, conviction id: $convictionId")
      return OffenceDto.from(conviction)
    } catch (e: WebClientException) {
      println(e.message)
      throw EntityNotFoundException("No offence found for crn: $crn, conviction id: $convictionId")
    }
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
