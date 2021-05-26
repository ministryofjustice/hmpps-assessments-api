package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CompleteAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateAssessmentResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OASysErrorResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.services.exceptions.ApiClientUnknownException
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.UserNotAuthorisedException

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
    deliusEvent: Long? = 123456
  ): Long? {
    log.info("Creating offender in OASys for crn: $crn, area: $area, user: $user, delius event: $deliusEvent")
    val path = "/offenders"
    return webClient
      .post(path, CreateOffenderDto(crn, area, user, deliusEvent))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) { handleOffenderError(crn, user, it, HttpMethod.POST, path) }
      .onStatus(HttpStatus::is5xxServerError) {
        throw ApiClientUnknownException(
          "Failed to create offender $crn in OASYs",
          HttpMethod.POST, path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(CreateOffenderResponseDto::class.java)
      .block()?.oasysOffenderId.also {
        log.info("Created offender in OASys for crn: $crn, area: $area, user: $user, delius event: $deliusEvent")
      }
  }

  fun createAssessment(
    offenderPK: Long,
    assessmentType: AssessmentType,
    user: String = "STUARTWHITLAM",
    area: String = "WWS",
  ): Long? {
    log.info("Creating Assessment of type $assessmentType in OASys for offender: $offenderPK, area: $area, user: $user")
    val path = "/assessments"
    return webClient
      .post(path, CreateAssessmentDto(offenderPK, area, user, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(
          offenderPK,
          user,
          assessmentType,
          it,
          HttpMethod.PUT,
          path
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        throw ApiClientUnknownException(
          "Failed to create assessment for offender $offenderPK in OASYs",
          HttpMethod.POST,
          path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(CreateAssessmentResponse::class.java)
      .block()?.oasysSetPk.also {
        log.info("Created Assessment of type $assessmentType in OASys for offender: $offenderPK, area: $area, user: $user")
      }
  }

  fun updateAssessment(
    offenderPK: Long,
    oasysSetPk: Long,
    assessmentType: AssessmentType,
    answers: Set<OasysAnswer>,
    user: String = "STUARTWHITLAM",
    area: String = "WWS",
  ): UpdateAssessmentAnswersResponseDto? {
    log.info("Updating answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $user, answers: $answers")
    val path = "/assessments"
    return webClient
      .put(path, UpdateAssessmentAnswersDto(oasysSetPk, offenderPK, area, user, answers, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(
          offenderPK,
          user,
          assessmentType,
          it,
          HttpMethod.PUT,
          path
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        throw ApiClientUnknownException(
          "Failed to update assessment for offender $offenderPK in OASYs",
          HttpMethod.PUT,
          path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(UpdateAssessmentAnswersResponseDto::class.java)
      .block().also {
        log.info("Updated answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $user")
      }
  }

  fun completeAssessment(
    offenderPK: Long,
    oasysSetPk: Long,
    assessmentType: AssessmentType,
    ignoreWarnings: Boolean = true,
    user: String = "STUARTWHITLAM",
    area: String = "WWS"
  ): UpdateAssessmentAnswersResponseDto? {
    log.info("Completing Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $user")
    val path = "/assessments/complete"
    return webClient
      .put(
        path,
        CompleteAssessmentDto(oasysSetPk, offenderPK, area, user, assessmentType, ignoreWarnings)
      )
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(offenderPK, user, assessmentType, it, HttpMethod.PUT, path)
      }
      .onStatus(HttpStatus::is5xxServerError) {
        throw ApiClientUnknownException(
          "Failed to complete assessment for offender $offenderPK in OASYs",
          HttpMethod.PUT,
          path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(UpdateAssessmentAnswersResponseDto::class.java)
      .block()
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
        log.error("Unable to create OASys offender. Duplicate OASys offender found for crn: $crn")
        clientResponse.bodyToMono(OASysErrorResponse::class.java)
          .map { error -> DuplicateOffenderRecordException(error.developerMessage) }
      }
      HttpStatus.FORBIDDEN == clientResponse.statusCode() -> {
        log.error("Unable to create OASys offender. User $user does not have permission to create offender with crn $crn")
        clientResponse.bodyToMono(OASysErrorResponse::class.java)
          .map { error -> UserNotAuthorisedException(error.developerMessage) }
      }
      else -> handleError(clientResponse, method, url)
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
        log.error("Unable to create OASys assessment. Existing assessment found for offender $offenderPK")
        clientResponse.bodyToMono(OASysErrorResponse::class.java)
          .map { error -> DuplicateOffenderRecordException(error.developerMessage) }
      }
      HttpStatus.FORBIDDEN == clientResponse.statusCode() -> {
        log.error("Unable to create OASys assessment. User $user does not have permission to create assessment type: $assessmentType for offender with pk $offenderPK")
        clientResponse.bodyToMono(OASysErrorResponse::class.java)
          .map { error -> UserNotAuthorisedException(error.developerMessage) }
      }
      else -> handleError(clientResponse, method, url)
    }
  }

  private fun handleError(clientResponse: ClientResponse, method: HttpMethod, url: String): Mono<out Throwable?>? {
    val httpStatus = clientResponse.statusCode()
    log.error("Unexpected exception with status $httpStatus")
    return clientResponse.bodyToMono(String::class.java).map { error ->
      ApiClientUnknownException(error, method, url, ExternalService.ASSESSMENTS_UPDATE)
    }.or(
      Mono.error(
        ApiClientUnknownException(
          "Unexpected exception with no body and status $httpStatus",
          method,
          url,
          ExternalService.ASSESSMENTS_UPDATE
        )
      )
    )
  }
}
