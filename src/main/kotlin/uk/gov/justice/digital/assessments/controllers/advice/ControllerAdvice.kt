package uk.gov.justice.digital.assessments.controllers.advice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConversionException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.services.exceptions.CannotCloseEpisodeException
import uk.gov.justice.digital.assessments.services.exceptions.CrnIsMandatoryException
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.services.exceptions.MdcPropertyException
import uk.gov.justice.digital.assessments.services.exceptions.MultipleExternalSourcesException
import uk.gov.justice.digital.assessments.services.exceptions.UpdateClosedEpisodeException
import uk.gov.justice.digital.assessments.services.exceptions.UserAreaHeaderIsMandatoryException
import uk.gov.justice.digital.assessments.services.exceptions.UserNotAuthorisedException
import org.springframework.security.access.AccessDeniedException as SpringSecurityAccessDeniedException

@ControllerAdvice
class ControllerAdvice {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @ExceptionHandler(UserNotAuthorisedException::class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  fun handle(e: UserNotAuthorisedException): ResponseEntity<ErrorResponse?> {
    log.error("UserNotAuthorisedException: ${e.message} with extra information ${e.extraInfoMessage}")
    return ResponseEntity(ErrorResponse(status = 403, developerMessage = e.message), HttpStatus.FORBIDDEN)
  }

  @ExceptionHandler(UpdateClosedEpisodeException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: UpdateClosedEpisodeException): ResponseEntity<ErrorResponse?> {
    log.info("UpdateClosedEpisodeException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(DuplicateOffenderRecordException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: DuplicateOffenderRecordException): ResponseEntity<ErrorResponse?> {
    log.error("DuplicateOffenderRecordException: ${e.message} with extra information ${e.extraInfoMessage}")
    return ResponseEntity(
      ErrorResponse(status = 400, developerMessage = e.message, reason = e.reason.toString()),
      HttpStatus.BAD_REQUEST,
    )
  }

  @ExceptionHandler(EntityNotFoundException::class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handle(e: EntityNotFoundException): ResponseEntity<ErrorResponse?> {
    log.info("EntityNotFoundException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 404, developerMessage = e.message), HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse?> {
    log.info("MethodArgumentNotValidException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(HttpMessageConversionException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: HttpMessageConversionException): ResponseEntity<*> {
    log.error("HttpMessageConversionException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(HttpMessageNotReadableException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse?> {
    log.error("HttpMessageNotReadableException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(IllegalArgumentException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: IllegalArgumentException): ResponseEntity<ErrorResponse?> {
    log.error("IllegalArgumentException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(ExternalApiEntityNotFoundException::class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handle(e: ExternalApiEntityNotFoundException): ResponseEntity<ErrorResponse?> {
    log.info(
      "ApiClientEntityNotFoundException for external client ${e.client} method ${e.method} and url ${e.url}: {}",
      e.message,
    )
    return ResponseEntity(ErrorResponse(status = 404, developerMessage = e.message), HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(ExternalApiUnknownException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handle(e: ExternalApiUnknownException): ResponseEntity<ErrorResponse?> {
    log.error(
      "ExternalClientUnknownException for external client ${e.client} method ${e.method} and url ${e.url}: {}",
      e.message,
    )
    return ResponseEntity(ErrorResponse(status = 500, developerMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
  }

  @ExceptionHandler(ExternalApiInvalidRequestException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: ExternalApiInvalidRequestException): ResponseEntity<ErrorResponse?> {
    log.error(
      "InvalidRequestException for external client ${e.client} method ${e.method} and url ${e.url}: {}",
      e.message,
    )
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(ExternalApiAuthorisationException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  fun handle(e: ExternalApiAuthorisationException): ResponseEntity<ErrorResponse?> {
    log.error(
      "ApiClientAuthorisationException for external client ${e.client} method ${e.method} and url ${e.url}: {}",
      e.message,
    )
    return ResponseEntity(ErrorResponse(status = 401, developerMessage = e.message), HttpStatus.UNAUTHORIZED)
  }

  @ExceptionHandler(UserAreaHeaderIsMandatoryException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: UserAreaHeaderIsMandatoryException): ResponseEntity<ErrorResponse?> {
    log.info("UserAreaHeaderIsMandatoryException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(MultipleExternalSourcesException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: MultipleExternalSourcesException): ResponseEntity<ErrorResponse?> {
    log.info("MultipleExternalSourcesException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(CrnIsMandatoryException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: CrnIsMandatoryException): ResponseEntity<ErrorResponse?> {
    log.info("CrnIsMandatoryException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(MdcPropertyException::class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  fun handle(e: MdcPropertyException): ResponseEntity<ErrorResponse?> {
    log.info("UserIdIsMandatoryException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 403, developerMessage = e.message), HttpStatus.FORBIDDEN)
  }

  @ExceptionHandler(SpringSecurityAccessDeniedException::class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  fun handle(e: SpringSecurityAccessDeniedException): ResponseEntity<ErrorResponse?> {
    log.info("SpringSecurityAccessDeniedException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 403, developerMessage = e.message), HttpStatus.FORBIDDEN)
  }

  @ExceptionHandler(ExternalApiForbiddenException::class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  fun handle(e: ExternalApiForbiddenException): ResponseEntity<ErrorResponse?> {
    log.error(
      "ApiClientForbiddenException for external client ${e.client} method ${e.method} and url ${e.url}: {}",
      e.message,
    )
    return ResponseEntity(
      ErrorResponse(
        status = 403,
        developerMessage = e.message,
        moreInfo = e.moreInfo,
        reason = e.reason.toString(),
      ),
      HttpStatus.FORBIDDEN,
    )
  }

  @ExceptionHandler(CannotCloseEpisodeException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: CannotCloseEpisodeException): ResponseEntity<ErrorResponse?> {
    log.info("CannotCloseEpisodeException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(Exception::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handle(e: Exception): ResponseEntity<ErrorResponse?> {
    log.error("Exception: ", e)
    return ResponseEntity(
      ErrorResponse(
        status = 500,
        developerMessage = "Internal Server Error. Check Logs",
        userMessage = "An unexpected error has occurred",
      ),
      HttpStatus.INTERNAL_SERVER_ERROR,
    )
  }
}
