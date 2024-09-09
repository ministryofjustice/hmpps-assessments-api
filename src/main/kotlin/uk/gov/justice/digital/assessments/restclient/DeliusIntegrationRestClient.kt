package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.UserAccessResponse
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.services.exceptions.ExceptionReason
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException

@Component
class DeliusIntegrationRestClient {

  @Autowired
  @Qualifier("deliusIntegrationClient")
  internal lateinit var webClient: AuthenticatingRestClient

  @Cacheable("caseDetails")
  fun getCaseDetails(crn: String, eventId: Long): CaseDetails? {
    log.info("Client retrieving case details for crn: $crn")
    val path = "/case-data/$crn/$eventId"
    return performHttpGet(path, "Failed to retrieve case details for crn: $crn")
      .bodyToMono(CaseDetails::class.java)
      .block().also {
        log.info("Case details found for crn: $crn, eventId: $eventId, found in ${ExternalService.DELIUS_INTEGRATIONS.name}")
      }
  }

  @Cacheable("verifyUserAccess")
  fun verifyUserAccess(crn: String, deliusUsername: String) {
    log.info("Client retrieving LAO details for crn: $crn")
    val path = "/users/$deliusUsername/access/$crn"
    val response = performHttpGet(path, "Failed to retrieve LAO info for user: $deliusUsername and crn: $crn")
      .bodyToMono(UserAccessResponse::class.java)
      .block()

    if (response != null) {
      if(response.userExcluded || response.userRestricted){
        throw ExternalApiForbiddenException(
          "User does not have permission to access offender with CRN $crn",
          HttpMethod.GET,
          path,
          ExternalService.DELIUS_INTEGRATIONS,
          listOfNotNull(response.exclusionMessage, response.restrictionMessage),
          ExceptionReason.LAO_PERMISSION,
        )
      }
    }
  }

  private fun performHttpGet(
    path: String,
    errorMessage: String,
  ): WebClient.ResponseSpec = webClient
    .get(path)
    .retrieve()
    .onStatus({ it.is4xxClientError }) {
      handle4xxError(
        it,
        HttpMethod.GET,
        path,
        ExternalService.DELIUS_INTEGRATIONS,
      )
    }
    .onStatus({ it.is5xxServerError }) {
      handle5xxError(
        errorMessage,
        HttpMethod.GET,
        path,
        ExternalService.DELIUS_INTEGRATIONS,
      )
    }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
