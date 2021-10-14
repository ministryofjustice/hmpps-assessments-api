package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.UserAccessResponse
import uk.gov.justice.digital.assessments.services.exceptions.ExceptionReason
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffendersPage
import uk.gov.justice.digital.assessments.utils.offenderStubResource.PrimaryId
import javax.persistence.EntityNotFoundException

@Component
class CommunityApiRestClient {
  @Autowired
  @Qualifier("communityApiWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  fun getOffender(crn: String): CommunityOffenderDto? {
    return getOffender(crn, CommunityOffenderDto::class.java);
  }

  fun getOffenderJson(crn: String): String? {
    return getOffender(crn, String::class.java);
  }

  private fun <T> getOffender(crn: String, elementClass: Class<T>): T? {
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
      .bodyToMono(elementClass)
      .block().also {
        log.info("Offender for crn: $crn, found in ${ExternalService.COMMUNITY_API.name}")
      }
  }

  fun getConvictions(crn: String): List<CommunityConvictionDto>? {
    log.info("Client retrieving conviction details for crn: $crn")
    val path = "secure/offenders/crn/$crn/convictions"
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
          "Failed to retrieve conviction details for crn: $crn",
          HttpMethod.GET,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .bodyToMono(object : ParameterizedTypeReference<List<CommunityConvictionDto>>() {})
      .block()
  }

  fun getPrimaryIds(page: Int, pageSize: Int): List<PrimaryId>? {
    val path = "/secure/offenders/primaryIdentifiers?includeActiveOnly=true&page=$page&size=$pageSize"
    log.info("Retrieving CRNs from $path")

    val offendersPage = webClient
      .get(path)
      .retrieve()
      .bodyToMono(OffendersPage::class.java)
      .block()

    log.info("Retrieved ${offendersPage?.content?.size} offender stubs")
    return offendersPage?.content ?: throw EntityNotFoundException("Failed to retrieve CRNs from Community API")
  }

  // TODO:: This method needs to be cached
  fun verifyUserAccess(crn: String, deliusUsername: String): UserAccessResponse {
    log.info("Client retrieving LAO details for crn: $crn")
    val path = "/secure/offenders/crn/$crn/user/$deliusUsername/userAccess"
    return webClient
      .get(path)
      .retrieve()
      .onStatus({ it == HttpStatus.FORBIDDEN }, {
        it.bodyToMono(UserAccessResponse::class.java)
          .map { error ->
            ExternalApiForbiddenException(
              "User does not have permission to access offender with CRN $crn",
              HttpMethod.GET,
              path,
              ExternalService.COMMUNITY_API,
              listOfNotNull(error.exclusionMessage, error.restrictionMessage),
              ExceptionReason.LAO_PERMISSION
            )
          }
      })
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
          "Failed to retrieve LAO details for crn: $crn",
          HttpMethod.GET,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .bodyToMono(UserAccessResponse::class.java)
      .block() ?: throw ExternalApiUnknownException(
      "No response returned from delius LAO check for crn $crn",
      HttpMethod.GET,
      path,
      ExternalService.COMMUNITY_API
    )
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
