package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import java.util.UUID

@Component
class AssessRisksAndNeedsApiRestClient {

  @Autowired
  @Qualifier("assessRisksAndNeedsApiWebClient")
  internal lateinit var webClient: RestClient

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun getRiskPredictors(
    predictorType: PredictorType,
    offenderAndOffencesDto: OffenderAndOffencesDto,
    final: Boolean,
    episodeUuid: UUID,
  ): RiskPredictorsDto? {
    log.info("Calculating Risk Predictors for $predictorType")
    val path =
      "/risks/predictors/$predictorType?final=$final&source=${ExternalService.ASSESSMENTS_API}&sourceId=$episodeUuid"
    return webClient
      .post(path, offenderAndOffencesDto)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.POST,
          path,
          ExternalService.ASSESS_RISKS_AND_NEEDS_API,
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to calculate and retrieve Risk Predictors $predictorType for crn ${offenderAndOffencesDto.crn}",
          HttpMethod.POST,
          path,
          ExternalService.ASSESS_RISKS_AND_NEEDS_API,
        )
      }
      .bodyToMono(RiskPredictorsDto::class.java)
      .block().also { log.info("Retrieve Risk Predictors $predictorType for crn ${offenderAndOffencesDto.crn}") }
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
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.POST,
          path,
          ExternalService.ASSESS_RISKS_AND_NEEDS_API,
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
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
