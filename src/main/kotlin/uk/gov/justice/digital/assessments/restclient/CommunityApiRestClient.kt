package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.assessments.api.UploadedUpwDocumentDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistrations
import uk.gov.justice.digital.assessments.restclient.communityapi.UserAccessResponse
import uk.gov.justice.digital.assessments.services.exceptions.ExceptionReason
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException

@Component
class CommunityApiRestClient(
  @Qualifier("communityApiWebClient")
  val webClient: WebClient
) {

  fun getOffender(crn: String): CommunityOffenderDto? {
    log.info("Client retrieving offender details for crn: $crn")
    val path = "secure/offenders/crn/$crn/all"
    return performHttpGet(path, "Failed to retrieve offender details for crn: $crn")
      .bodyToMono(CommunityOffenderDto::class.java)
      .block().also {
        log.info("Offender for crn: $crn, found in ${ExternalService.COMMUNITY_API.name}")
      }
  }

  private fun performHttpGet(
    path: String,
    errorMessage: String
  ): WebClient.ResponseSpec = webClient
    .get()
    .uri(path)
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
        errorMessage,
        HttpMethod.GET,
        path,
        ExternalService.COMMUNITY_API
      )
    }
  //
  // fun getConvictions(crn: String): List<CommunityConvictionDto>? {
  //   log.info("Client retrieving conviction details for crn: $crn")
  //   val path = "secure/offenders/crn/$crn/convictions"
  //   return performHttpGet(path, "Failed to retrieve conviction details for crn: $crn")
  //     .bodyToMono(object : ParameterizedTypeReference<List<CommunityConvictionDto>>() {})
  //     .block()
  // }
  //
  // fun getConviction(crn: String, convictionId: Long): CommunityConvictionDto? {
  //   log.info("Client retrieving conviction details for crn: $crn")
  //   val path = "secure/offenders/crn/$crn/convictions/$convictionId"
  //   return performHttpGet(path, "Failed to retrieve conviction details for crn: $crn and conviction ID $convictionId")
  //     .bodyToMono(CommunityConvictionDto::class.java)
  //     .block()
  // }

  @Cacheable("riskRegistrations")
  fun getRegistrations(crn: String): CommunityRegistrations? {
    log.info("Client retrieving registrations for crn: $crn")
    val path = "secure/offenders/crn/$crn/registrations"
    return performHttpGet(path, "Failed to retrieve registrations for crn: $crn")
      .bodyToMono(object : ParameterizedTypeReference<CommunityRegistrations>() {})
      .block()
  }

  @Cacheable("verifyUserAccess")
  fun verifyUserAccess(crn: String, deliusUsername: String): UserAccessResponse {
    log.info("Client retrieving LAO details for crn: $crn")
    val path = "/secure/offenders/crn/$crn/user/$deliusUsername/userAccess"
    return webClient
      .get()
      .uri(path)
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

  fun uploadDocumentToDelius(crn: String, convictionId: Long, fileData: MultipartFile): UploadedUpwDocumentDto? {
    val path = "/secure/offenders/crn/$crn/convictions/$convictionId/document"
    val builder = MultipartBodyBuilder()
    builder.part("fileData", fileData.resource)
    return webClient
      .post()
      .uri(path)
      .bodyValue(builder.build())
      .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.POST,
          path,
          ExternalService.COMMUNITY_API
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to upload UPW document to community-api",
          HttpMethod.POST, path,
          ExternalService.COMMUNITY_API
        )
      }
      .bodyToMono(UploadedUpwDocumentDto::class.java)
      .block()
  }

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
}
