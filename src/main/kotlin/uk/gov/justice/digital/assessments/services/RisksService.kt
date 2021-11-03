package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.RegistrationsDto
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient

@Service
class RisksService(
  private val communityApiRestClient: CommunityApiRestClient,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getRegistrationsForAssessment(crn: String): RegistrationsDto {
    log.info("Getting registrations for crn: $crn")
    val response = communityApiRestClient.getRegistrations(crn)
      ?: throw Exception("Failed to get registrations for assessments")

    return RegistrationsDto.from(response.registrations.toList())
  }
}
