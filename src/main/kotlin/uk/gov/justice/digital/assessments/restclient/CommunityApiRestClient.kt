package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
    return webClient
      .get("secure/offenders/crn/$crn/all")
      .retrieve()
      .bodyToMono(CommunityOffenderDto::class.java)
      .block()
  }

  fun getConviction(crn: String, convictionId: Long): CommunityConvictionDto? {
    log.info("Client retrieving conviction details for crn: $crn, conviction id: $convictionId")
    return webClient
      .get("secure/offenders/crn/$crn/conviction/$convictionId")
      .retrieve()
      .bodyToMono(CommunityConvictionDto::class.java)
      .block()
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
