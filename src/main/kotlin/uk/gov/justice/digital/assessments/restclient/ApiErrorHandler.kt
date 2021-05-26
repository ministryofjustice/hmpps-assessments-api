package uk.gov.justice.digital.assessments.restclient

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OASysErrorResponse
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientUnknownException
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.UserNotAuthorisedException

fun handle4xxError(
  clientResponse: ClientResponse,
  method: HttpMethod,
  url: String,
  client: ExternalService
): Mono<out Throwable?>? {
  return when (clientResponse.statusCode()) {
    HttpStatus.BAD_REQUEST -> {
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientInvalidRequestException(error.developerMessage, method, url, client) }
    }
    HttpStatus.UNAUTHORIZED -> {
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientAuthorisationException(error.developerMessage, method, url, client) }
    }
    HttpStatus.FORBIDDEN -> {
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientForbiddenException(error.developerMessage, method, url, client) }
    }
    HttpStatus.NOT_FOUND -> {
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientEntityNotFoundException(error.developerMessage, method, url, client) }
    }
    else -> handleError(clientResponse, method, url, client)
  }
}

fun handle5xxError(
  message: String,
  method: HttpMethod,
  path: String,
  service: ExternalService
): Mono<out Throwable?>? {
  throw ApiClientUnknownException(
    message,
    method,
    path,
    service
  )
}

fun handleOffenderError(
  crn: String?,
  user: String?,
  clientResponse: ClientResponse,
  method: HttpMethod,
  url: String
): Mono<out Throwable?>? {
  return when {
    HttpStatus.CONFLICT == clientResponse.statusCode() -> {
      AssessmentUpdateRestClient.log.error("Unable to create OASys offender. Duplicate OASys offender found for crn: $crn")
      clientResponse.bodyToMono(OASysErrorResponse::class.java)
        .map { error -> DuplicateOffenderRecordException(error.developerMessage) }
    }
    HttpStatus.FORBIDDEN == clientResponse.statusCode() -> {
      AssessmentUpdateRestClient.log.error("Unable to create OASys offender. User $user does not have permission to create offender with crn $crn")
      clientResponse.bodyToMono(OASysErrorResponse::class.java)
        .map { error -> UserNotAuthorisedException(error.developerMessage) }
    }
    else -> handleError(clientResponse, method, url, ExternalService.ASSESSMENTS_API)
  }
}

fun handleAssessmentError(
  offenderPK: Long?,
  user: String?,
  assessmentType: AssessmentType,
  clientResponse: ClientResponse,
  method: HttpMethod,
  url: String
): Mono<out Throwable?>? {
  return when {
    HttpStatus.CONFLICT == clientResponse.statusCode() -> {
      AssessmentUpdateRestClient.log.error("Unable to create OASys assessment. Existing assessment found for offender $offenderPK")
      clientResponse.bodyToMono(OASysErrorResponse::class.java)
        .map { error -> DuplicateOffenderRecordException(error.developerMessage) }
    }
    HttpStatus.FORBIDDEN == clientResponse.statusCode() -> {
      AssessmentUpdateRestClient.log.error("Unable to create OASys assessment. User $user does not have permission to create assessment type: $assessmentType for offender with pk $offenderPK")
      clientResponse.bodyToMono(OASysErrorResponse::class.java)
        .map { error -> UserNotAuthorisedException(error.developerMessage) }
    }
    else -> handleError(clientResponse, method, url, ExternalService.ASSESSMENTS_API)
  }
}

fun handleError(
  clientResponse: ClientResponse,
  method: HttpMethod,
  url: String,
  client: ExternalService
): Mono<out Throwable?>? {
  val httpStatus = clientResponse.statusCode()
  return clientResponse.bodyToMono(String::class.java).map { error ->
    ApiClientUnknownException(error, method, url, client)
  }.or(
    Mono.error(
      ApiClientUnknownException(
        "Unexpected exception with no body and status $httpStatus",
        method,
        url,
        client
      )
    )
  )
}