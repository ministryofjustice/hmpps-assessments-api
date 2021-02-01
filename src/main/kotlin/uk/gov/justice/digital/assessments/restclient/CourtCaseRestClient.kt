package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase

@Component
class CourtCaseRestClient {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Autowired
  @Qualifier("courtCaseWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  @Value("\${court-case-api.case-path-template}")
  internal lateinit var casePathTemplate: String

  fun getCourtCase(courtCode: String, caseNumber: String): CourtCase? {
    return fetchCourtCase(courtCode, caseNumber, CourtCase::class.java)
  }

  fun getCourtCaseJson(courtCode: String, caseNumber: String): String? {
    return fetchCourtCase(courtCode, caseNumber, String::class.java)
  }

  private fun <T> fetchCourtCase(courtCode: String, caseNumber: String, elementClass: Class<T>): T? {
    log.info("Retrieving court case for court: $courtCode,  caseNo: $caseNumber")
    val path = String.format(casePathTemplate, courtCode, caseNumber)
    return webClient
      .get(path)
      .retrieve()
      .bodyToMono(elementClass)
      .block()
  }
}
