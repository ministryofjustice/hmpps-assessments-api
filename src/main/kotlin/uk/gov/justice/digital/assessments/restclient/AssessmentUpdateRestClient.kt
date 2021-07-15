package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType
import uk.gov.justice.digital.assessments.redis.UserDetailsRedisRepository
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Authorized
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RoleNames
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Roles
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CompleteAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateAssessmentResponse
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OasysAnswer
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.UpdateAssessmentAnswersResponseDto
import uk.gov.justice.digital.assessments.services.AssessmentSchemaService
import uk.gov.justice.digital.assessments.utils.RequestData

@Component
class AssessmentUpdateRestClient {
  @Autowired
  @Qualifier("assessmentUpdateWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  @Autowired
  internal lateinit var userDetailsRedisRepository: UserDetailsRedisRepository

  @Autowired
  internal lateinit var assessmentSchemaService: AssessmentSchemaService

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  /*
  * Use this method to conditionally Push Offenders and Assessments to Oasys depending on the Assessment Schema Code
  * DO NOT CHANGE THIS METHOD SIGNATURE as it does work with PushToOasysAspect to decide for which assessments
  * we push data into Oasys
  * */
  fun pushToOasys(
    crn: String?,
    deliusEventId: Long? = null,
    assessmentSchemaCode: AssessmentSchemaCode?
  ): Pair<Long?, Long?> {
    val oasysOffenderPk = crn?.let { createOasysOffender(crn = crn, deliusEvent = deliusEventId) }
    val oasysAssessmentType = assessmentSchemaService.toOasysAssessmentType(assessmentSchemaCode)
    val oasysSetPK = oasysOffenderPk?.let { createAssessment(it, oasysAssessmentType) }
    return Pair(oasysOffenderPk, oasysSetPK)
  }

  /*
  * Use this method only if you want to force Creating Offender in Oasys
  * */
  @Authorized(roleChecks = [Roles.RBAC_OTHER], roleNames = [RoleNames.CREATE_OFFENDER])
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

  /*
  * Use this method only if you want to force Creating Assessment in Oasys
  * */
  @Authorized(roleChecks = [Roles.OFF_ASSESSMENT_CREATE])
  fun createAssessment(
    offenderPK: Long,
    oasysAssessmentType: OasysAssessmentType,
  ): Long? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Creating Assessment of type $oasysAssessmentType in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
    val path = "/assessments"
    return webClient
      .post(path, CreateAssessmentDto(offenderPK, area, oasysUserCode, oasysAssessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(
          offenderPK,
          oasysUserCode,
          oasysAssessmentType,
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
        log.info("Created Assessment of type $oasysAssessmentType in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
      }
  }

  @Authorized(roleChecks = [Roles.ASSESSMENT_EDIT])
  fun updateAssessment(
    offenderPK: Long,
    oasysAssessmentType: OasysAssessmentType,
    oasysSetPk: Long,
    answers: Set<OasysAnswer>,
  ): UpdateAssessmentAnswersResponseDto? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Updating answers for Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode, answers: $answers")
    val path = "/assessments"
    return webClient
      .put(path, UpdateAssessmentAnswersDto(oasysSetPk, offenderPK, area, oasysUserCode, answers, oasysAssessmentType))
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(
          offenderPK,
          oasysUserCode,
          oasysAssessmentType,
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

  @Authorized(roleChecks = [Roles.ASSESSMENT_EDIT])
  fun completeAssessment(
    offenderPK: Long,
    oasysAssessmentType: OasysAssessmentType,
    oasysSetPk: Long,
    ignoreWarnings: Boolean = true,
  ): UpdateAssessmentAnswersResponseDto? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    log.info("Completing Assessment $oasysSetPk in OASys for offender: $offenderPK, area: $area, user: $oasysUserCode")
    val path = "/assessments/complete"
    return webClient
      .put(
        path,
        CompleteAssessmentDto(oasysSetPk, offenderPK, area, oasysUserCode, oasysAssessmentType, ignoreWarnings)
      )
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(offenderPK, oasysUserCode, oasysAssessmentType, it, HttpMethod.PUT, path)
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
