package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.redis.UserDetailsRedisRepository
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Authorized
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Roles
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CompleteAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateAssessmentResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.utils.RequestData

@Component
class AssessmentUpdateRestClient {
  @Autowired
  @Qualifier("assessmentUpdateWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  @Autowired
  internal lateinit var userDetailsRedisRepository: UserDetailsRedisRepository

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun createOasysOffender(
    crn: String,
    deliusEvent: Long? = 123456
  ): Long? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Creating offender in OASys for crn: $crn, area: $area, user: $oasysUserCode, delius event: $deliusEvent")
    val path = "/offenders"
    return webClient
      .post(path, CreateOffenderDto(crn, area, oasysUserCode, deliusEvent))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) { handleOffenderError(crn, oasysUserCode, it, HttpMethod.POST, path) }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to create offender $crn in OASYs",
          HttpMethod.POST, path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(CreateOffenderResponseDto::class.java)
      .block()?.oasysOffenderId.also {
        log.info("Created offender in OASys for crn: $crn, area: $area, user: $oasysUserCode, delius event: $deliusEvent")
      }
  }

  @Authorized(roleChecks = [Roles.OFF_ASSESSMENT_CREATE])
  fun createAssessment(
    offenderPK: Long,
    assessmentType: AssessmentType,
  ): Long? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Creating Assessment of type $assessmentType in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
    val path = "/assessments"
    return webClient
      .post(path, CreateAssessmentDto(offenderPK, area, oasysUserCode, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(
          offenderPK,
          oasysUserCode,
          assessmentType,
          it,
          HttpMethod.PUT,
          path
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to create assessment for offender $offenderPK in OASYs",
          HttpMethod.POST,
          path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(CreateAssessmentResponse::class.java)
      .block()?.oasysSetPk.also {
        log.info("Created Assessment of type $assessmentType in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
      }
  }

  @Authorized(roleChecks = [Roles.ASSESSMENT_EDIT])
  fun updateAssessment(
    offenderPK: Long,
    assessmentType: AssessmentType,
    oasysSetPk: Long,
    answers: Set<OasysAnswer>,
  ): UpdateAssessmentAnswersResponseDto? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Updating answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode, answers: $answers")
    val path = "/assessments"
    return webClient
      .put(path, UpdateAssessmentAnswersDto(oasysSetPk, offenderPK, area, oasysUserCode, answers, assessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(
          offenderPK,
          oasysUserCode,
          assessmentType,
          it,
          HttpMethod.PUT,
          path
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to update assessment for offender $offenderPK in OASYs",
          HttpMethod.PUT,
          path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(UpdateAssessmentAnswersResponseDto::class.java)
      .block().also {
        log.info("Updated answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
      }
  }

  fun completeAssessment(
    offenderPK: Long,
    oasysSetPk: Long,
    assessmentType: AssessmentType,
    ignoreWarnings: Boolean = true,
  ): UpdateAssessmentAnswersResponseDto? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Completing Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
    val path = "/assessments/complete"
    return webClient
      .put(
        path,
        CompleteAssessmentDto(oasysSetPk, offenderPK, area, oasysUserCode, assessmentType, ignoreWarnings)
      )
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(offenderPK, oasysUserCode, assessmentType, it, HttpMethod.PUT, path)
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to complete assessment for offender $offenderPK in OASYs",
          HttpMethod.PUT,
          path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(UpdateAssessmentAnswersResponseDto::class.java)
      .block()
  }
}
