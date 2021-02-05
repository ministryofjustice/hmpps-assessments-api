package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto
import uk.gov.justice.digital.assessments.services.exceptions.DuplicateOffenderRecordException
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
      .onStatus(
        { it == HttpStatus.FORBIDDEN },
        {
          log.error("User $user does not have permission to create offender with crn $crn")
          it.bodyToMono(ErrorResponse::class.java)
            .flatMap { error -> Mono.error(UserNotAuthorisedException(error.developerMessage)) }
        }
      )
      .onStatus(
        { it == HttpStatus.CONFLICT },
        {
          log.error("Duplicate records found for $crn")
          it.bodyToMono(ErrorResponse::class.java)
            .flatMap { error -> Mono.error(DuplicateOffenderRecordException(error.developerMessage)) }
        }
      )
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
      .onStatus(
        { it == HttpStatus.FORBIDDEN },
        {
          log.error("User $user does not have permission to create assessment of type $assessmentType for offender $offenderPK")
          it.bodyToMono(ErrorResponse::class.java)
            .flatMap { error -> Mono.error(UserNotAuthorisedException(error.developerMessage)) }
        }
      )
      .onStatus(
        { it == HttpStatus.CONFLICT },
        {
          log.error("Existing assessment found for offender $offenderPK")
          it.bodyToMono(ErrorResponse::class.java)
            .flatMap { error -> Mono.error(DuplicateOffenderRecordException(error.developerMessage)) }
        }
      )
      .bodyToMono(CreateAssessmentResponse::class.java)
      .block()?.oasysSetPk
  }
}
