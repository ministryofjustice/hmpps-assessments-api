package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto

@Component
class AssessRisksAndNeedsApiRestClient {

  @Autowired
  @Qualifier("assessRisksAndNeedsApiWebClient")
  internal lateinit var webClient: RestClient

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Cacheable("roshRiskWidget")
  fun getRoshRiskSummary(
    crn: String,
  ): RoshRiskSummaryDto? {
    log.info("Fetching ROSH risk summary for crn $crn")
    val path =
      "/risks/crn/$crn/widget"
    return webClient
      .get(path)
      .retrieve()
      .onStatus({ it.is4xxClientError }) {
        handle4xxError(
          it,
          HttpMethod.POST,
          path,
          ExternalService.ASSESS_RISKS_AND_NEEDS_API,
        )
      }
      .onStatus({ it.is5xxServerError }) {
        handle5xxError(
          "Failed to fetch ROSH risk summary for crn $crn",
          HttpMethod.POST,
          path,
          ExternalService.ASSESS_RISKS_AND_NEEDS_API,
        )
      }
      .bodyToMono(RoshRiskSummaryDto::class.java)
      .block().also { log.info("Fetched ROSH risk summary for crn $crn") }
  }
}
