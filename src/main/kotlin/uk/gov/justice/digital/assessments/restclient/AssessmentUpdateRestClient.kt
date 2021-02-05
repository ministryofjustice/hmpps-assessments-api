package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.UserNotAuthorisedException
import uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api.CreateAssessmentDto
import uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api.CreateAssessmentResponse
import java.nio.charset.StandardCharsets

@Component
class AssessmentUpdateRestClient {
  @Autowired
  @Qualifier("assessmentUpdateWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun createOasysOffender(
    crn: String,
    user: String = "STUARTWHITLAM",
    area: String = "WWS",
    deliusEvent: Int = 123456
  ): Long? {
    log.info("Creating offender in OASys for crn: $crn, area: $area, user: $user, delius event: $deliusEvent")
    return webClient
      .post("/offenders", CreateOffenderDto(crn, area, user, deliusEvent))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) { handleOffenderError(crn, user, it) }
      .bodyToMono(CreateOffenderResponseDto::class.java)
      .doOnError { log.error("Unexpected exception when creating offender with crn $crn") }
      .block()?.oasysOffenderId
  }

  fun createAssessment(
    offenderPK: Long,
    assessmentType: String,
    user: String = "STUARTWHITLAM",
    area: String = "WWS",
  ): Long? {
    log.info("Creating Assessment of type $assessmentType in OASys for offender: $offenderPK, area: $area, user: $user")
    return webClient
      .post("/assessments", CreateAssessmentDto(offenderPK, area, user, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) { handleAssessmentError(offenderPK, user, assessmentType, it) }
      .bodyToMono(CreateAssessmentResponse::class.java)
      .block()?.oasysSetPk
  }

  fun handleOffenderError(crn: String?, user: String?, clientResponse: ClientResponse): Mono<out Throwable?>? {
    return when {
      HttpStatus.CONFLICT == clientResponse.statusCode() -> {
        log.error("Duplicate OASys offender found for crn: $crn")
        clientResponse.bodyToMono(ErrorResponse::class.java)
          .flatMap { error -> Mono.error(DuplicateOffenderRecordException(error.developerMessage)) }
      }
      HttpStatus.FORBIDDEN == clientResponse.statusCode() -> {
        log.error("User $user does not have permission to create offender with crn $crn")
        clientResponse.bodyToMono(ErrorResponse::class.java)
          .flatMap { error -> Mono.error(UserNotAuthorisedException(error.developerMessage)) }
      }
      else -> handleError(clientResponse)
    }
  }

  fun handleAssessmentError(offenderPK: Long?, user: String?, assessmentType: String?, clientResponse: ClientResponse): Mono<out Throwable?>? {
    return when {
      HttpStatus.CONFLICT == clientResponse.statusCode() -> {
        log.error("Existing assessment found for offender $offenderPK")
        clientResponse.bodyToMono(ErrorResponse::class.java)
          .flatMap { error -> Mono.error(DuplicateOffenderRecordException(error.developerMessage)) }
      }
      HttpStatus.FORBIDDEN == clientResponse.statusCode() -> {
        log.error("User $user does not have permission to create assessment type: $assessmentType for offender with pk $offenderPK")
        clientResponse.bodyToMono(ErrorResponse::class.java)
          .flatMap { error -> Mono.error(UserNotAuthorisedException(error.developerMessage)) }
      }
      else -> handleError(clientResponse)
    }
  }

  private fun handleError(clientResponse: ClientResponse): Mono<out Throwable?>? {
    val httpStatus = clientResponse.statusCode()
    log.error("Unexpected exception with status $httpStatus")
    throw WebClientResponseException.create(
      httpStatus.value(),
      httpStatus.name,
      clientResponse.headers().asHttpHeaders(),
      clientResponse.toString().toByteArray(),
      StandardCharsets.UTF_8
    )
  }
}
