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
import uk.gov.justice.digital.assessments.services.exceptions.*

@ControllerAdvice
class ControllerAdvice {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @ExceptionHandler(EntityNotFoundException::class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  fun handle(e: EntityNotFoundException): ResponseEntity<ErrorResponse?> {
    log.info("EntityNotFoundException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 404, developerMessage = e.message), HttpStatus.NOT_FOUND)
  }

  @ExceptionHandler(UpdateClosedEpisodeException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: UpdateClosedEpisodeException): ResponseEntity<ErrorResponse?> {
    log.info("UpdateClosedEpisodeException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
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

  @ExceptionHandler(OASysClientException::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handle(e: OASysClientException): ResponseEntity<ErrorResponse?> {
    log.error("OASysClientException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 500, developerMessage = e.message), HttpStatus.INTERNAL_SERVER_ERROR)
  }

  @ExceptionHandler(ReferenceDataInvalidRequestException::class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  fun handle(e: ReferenceDataInvalidRequestException): ResponseEntity<ErrorResponse?> {
    log.error("ReferenceDataInvalidRequestException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 400, developerMessage = e.message), HttpStatus.BAD_REQUEST)
  }

  @ExceptionHandler(ReferenceDataAuthorisationException::class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  fun handle(e: ReferenceDataAuthorisationException): ResponseEntity<ErrorResponse?> {
    log.error("ReferenceDataAuthorisationException: {}", e.message)
    return ResponseEntity(ErrorResponse(status = 401, developerMessage = e.message), HttpStatus.UNAUTHORIZED)
  }

  @ExceptionHandler(Exception::class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  fun handle(e: Exception): ResponseEntity<ErrorResponse?> {
    log.error("Exception: {}", e.message)
    return ResponseEntity(
      ErrorResponse(
        status = 500,
        developerMessage = "Internal Server Error. Check Logs",
        userMessage = "An unexpected error has occurred"
      ),
      HttpStatus.INTERNAL_SERVER_ERROR
    )
  }
}
