package uk.gov.justice.digital.assessments.restclient

import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientUnknownException

fun handle4xxError(
  clientResponse: ClientResponse,
  method: HttpMethod,
  url: String,
  client: ExternalService,
  fieldName: String? = ""
): Mono<out Throwable?>? {
  return when (clientResponse.statusCode()) {
    HttpStatus.BAD_REQUEST -> {
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientInvalidRequestException(error.developerMessage, method, url, client, fieldName) }
    }
    HttpStatus.UNAUTHORIZED -> {
      // log.error("Unauthorised for $method $url with param $fieldName")
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientAuthorisationException(error.developerMessage, method, url, client) }
    }
    HttpStatus.FORBIDDEN -> {
      // log.error("Unauthorised for $method $url with param $fieldName")
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientForbiddenException(error.developerMessage, method, url, client) }
    }
    HttpStatus.NOT_FOUND -> {
      // log.error("Not found for $method $url with param $fieldName")
      clientResponse.bodyToMono(ApiErrorResponse::class.java)
        .map { error -> ApiClientEntityNotFoundException(error.developerMessage, method, url, client) }
    }
    else -> handleError(clientResponse, method, url, client)
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
