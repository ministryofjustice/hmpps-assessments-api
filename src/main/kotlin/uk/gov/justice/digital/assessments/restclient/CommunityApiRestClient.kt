package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.communityapi.GetOffenderDto

@Component
class CommunityApiRestClient {
  @Autowired
  @Qualifier("communityApiWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  fun getOffender(crn: String): GetOffenderDto? {
    log.info("Client retrieving offender details for crn: $crn")
    return webClient
      .get("secure/offenders/crn/$crn")
      .retrieve()
      .bodyToMono(GetOffenderDto::class.java)
      .block()
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
