package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClientException
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.GetOffenderDto
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

@Service
class OffenderService(private val communityApiRestClient: CommunityApiRestClient) {

    fun getOffender(crn: String): GetOffenderDto? {
        try {
            log.info("Requesting offender details for crn: $crn")
            return communityApiRestClient.getOffender(crn)
                ?: throw EntityNotFoundException("No offender found for crn: $crn")
        } catch (e: WebClientException) {
            println(e.message)
            throw EntityNotFoundException("No offender found for crn: $crn")
        }
    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }
}