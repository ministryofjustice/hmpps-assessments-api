package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails

@Component
class DeliusIntegrationRestClient {

  @Autowired
  @Qualifier("deliusIntegrationClient")
  internal lateinit var webClient: AuthenticatingRestClient

  fun getCaseDetails(crn: String, eventId: Long): CaseDetails? {
    log.info("Client retrieving case details for crn: $crn")
    val path = "/case-data/$crn/$eventId"
    return performHttpGet(path, "Failed to retrieve case details for crn: $crn")
      .bodyToMono(CaseDetails::class.java)
      .block().also {
        log.info("Case details found for crn: $crn, eventId: $eventId, found in ${ExternalService.DELIUS_INTEGRATIONS.name}")
      }
  }

  private fun performHttpGet(
    path: String,
    errorMessage: String
  ): WebClient.ResponseSpec = webClient
    .get(path)
    .retrieve()
    .onStatus(HttpStatus::is4xxClientError) {
      handle4xxError(
        it,
        HttpMethod.GET,
        path,
        ExternalService.DELIUS_INTEGRATIONS
      )
    }
    .onStatus(HttpStatus::is5xxServerError) {
      handle5xxError(
        errorMessage,
        HttpMethod.GET,
        path,
        ExternalService.DELIUS_INTEGRATIONS
      )
    }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
