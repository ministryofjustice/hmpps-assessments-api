package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto

@Component
class CommunityApiRestClient {
  @Autowired
  @Qualifier("communityApiWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  fun getOffender(crn: String): CommunityOffenderDto? {
    log.info("Client retrieving offender details for crn: $crn")
    val path = "secure/offenders/crn/$crn/all"
    return webClient
      .get(path)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.GET,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve offender details for crn: $crn",
          HttpMethod.GET,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .bodyToMono(CommunityOffenderDto::class.java)
      .block()
  }

  fun getConviction(crn: String, convictionId: Long): CommunityConvictionDto? {
    log.info("Client retrieving conviction details for crn: $crn, conviction id: $convictionId")
    val path = "secure/offenders/crn/$crn/convictions/$convictionId"
    return webClient
      .get(path)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.GET,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve conviction details for crn: $crn, conviction id: $convictionId",
          HttpMethod.GET,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .bodyToMono(CommunityConvictionDto::class.java)
      .block()
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
