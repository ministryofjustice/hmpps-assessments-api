package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OASysErrorResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
import uk.gov.justice.digital.assessments.services.exceptions.OASysClientException
import uk.gov.justice.digital.assessments.services.exceptions.UserNotAuthorisedException
import uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api.CreateAssessmentDto
import uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api.CreateAssessmentResponse

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
      .onStatus(HttpStatus::is5xxServerError) { throw OASysClientException("Failed to create offender $crn in OASYs") }
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
    return webClient
      .post("/assessments", CreateAssessmentDto(offenderPK, area, user, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) { handleAssessmentError(offenderPK, user, assessmentType, it) }
      .onStatus(HttpStatus::is5xxServerError) { throw OASysClientException("Failed to create assessment for offender $offenderPK in OASYs") }
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
    log.info("Updating answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $user")
    return webClient
      .put("/assessments", UpdateAssessmentAnswersDto(oasysSetPk, offenderPK, area, user, answers, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) { handleAssessmentError(offenderPK, user, assessmentType, it) }
      .onStatus(HttpStatus::is5xxServerError) { throw OASysClientException("Failed to update assessment for offender $offenderPK in OASYs") }
      .bodyToMono(UpdateAssessmentAnswersResponseDto::class.java)
      .block().also {
        log.info("Updated answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $user")
      }
  }

  fun handleOffenderError(crn: String?, user: String?, clientResponse: ClientResponse): Mono<out Throwable?>? {
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
      else -> handleError(clientResponse)
    }
  }

  fun handleAssessmentError(
    offenderPK: Long?,
    user: String?,
    assessmentType: AssessmentType,
    clientResponse: ClientResponse
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
      else -> handleError(clientResponse)
    }
  }

  private fun handleError(clientResponse: ClientResponse): Mono<out Throwable?>? {
    val httpStatus = clientResponse.statusCode()
    log.error("Unexpected exception with status $httpStatus")
    return clientResponse.bodyToMono(String::class.java).map { error ->
      OASysClientException(error)
    }.or(Mono.error(OASysClientException("Unexpected exception with no body and status $httpStatus")))
  }
}
