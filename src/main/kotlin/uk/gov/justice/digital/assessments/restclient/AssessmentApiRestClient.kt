package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.ClientResponse
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.redis.UserDetailsRedisRepository
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Authorized
import uk.gov.justice.digital.assessments.restclient.assessmentapi.FilteredReferenceDataDto
import uk.gov.justice.digital.assessments.restclient.assessmentapi.OASysAssessmentDto
import uk.gov.justice.digital.assessments.restclient.assessmentapi.OASysRBACErrorResponse
import uk.gov.justice.digital.assessments.restclient.assessmentapi.OASysRBACPermissionsDto
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RBACPermissionsPayload
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RefElementDto
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RoleNames
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Roles
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.OASysErrorResponse
import uk.gov.justice.digital.assessments.services.exceptions.ExceptionReason
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.utils.RequestData

@Component
class AssessmentApiRestClient {
  @Autowired
  @Qualifier("assessmentApiWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  @Autowired
  internal lateinit var userDetailsRedisRepository: UserDetailsRedisRepository

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Authorized(roleChecks = [Roles.ASSESSMENT_READ])
  fun getOASysAssessment(
    offenderPK: Long,
    assessmentType: AssessmentType,
    oasysSetPk: Long,
  ): OASysAssessmentDto? {
    log.info("Retrieving OASys Assessment $oasysSetPk")
    val path = "/assessments/oasysSetPk/$oasysSetPk"
    return webClient
      .get(path)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handleAssessmentError(oasysSetPk, it, HttpMethod.GET, path)
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve Oasys assessment $oasysSetPk",
          HttpMethod.GET,
          path,
          ExternalService.ASSESSMENTS_API
        )
      }
      .bodyToMono(OASysAssessmentDto::class.java)
      .block().also { log.info("Retrieved OASys Assessment $oasysSetPk") }
  }

  private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

  fun getFilteredReferenceData(
    oasysSetPk: Long,
    offenderPk: Long?,
    assessmentType: String,
    sectionCode: String,
    fieldName: String,
    parentList: Map<String, String>?
  ): Map<String, Collection<RefElementDto>>? {
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode
    val path = "/referencedata/filtered"
    return webClient
      .post(
        path,
        FilteredReferenceDataDto(
          oasysSetPk,
          oasysUserCode,
          RequestData.getAreaCode(),
          offenderPk,
          assessmentType,
          sectionCode,
          fieldName,
          parentList
        )
      )
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.POST,
          path.plus(" for fieldName $fieldName"),
          ExternalService.ASSESSMENTS_API
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve OASys filtered reference data for $fieldName", HttpMethod.POST,
          path,
          ExternalService.ASSESSMENTS_API
        )
      }
      .bodyToMono(typeReference<Map<String, Collection<RefElementDto>>>())
      .block().also { log.info("Retrieved OASys filtered reference data for $fieldName") }
  }

  fun getOASysRBACPermissions(
    roleChecks: Set<Roles>,
    offenderPk: Long? = null,
    oasysSetPk: Long? = null,
    assessmentType: AssessmentType? = null,
    roleNames: Set<RoleNames>? = emptySet()
  ): RBACPermissionsPayload? {
    val area = RequestData.getAreaCode()
    val oasysUserCode = userDetailsRedisRepository.findByUserId(RequestData.getUserId()).oasysUserCode

    log.info("Retrieving OASys RBAC permissions for oasys user $oasysUserCode, area $area, offender $offenderPk, assessment $oasysSetPk and assessment type $assessmentType")
    val path = "/authorisation/permissions"
    val permissionsDto = OASysRBACPermissionsDto(
      oasysUserCode,
      roleChecks,
      area,
      offenderPk,
      oasysSetPk,
      assessmentType,
      roleNames
    )
    return webClient
      .post(
        path,
        permissionsDto
      )
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handlePermissionsError(permissionsDto, it, HttpMethod.POST, path)
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve OASys RBAC permissions for oasys user $oasysUserCode, area $area, offender $offenderPk, assessment $oasysSetPk and assessment type $assessmentType",
          HttpMethod.GET,
          path,
          ExternalService.ASSESSMENTS_API
        )
      }
      .bodyToMono(RBACPermissionsPayload::class.java)
      .block().also { log.info("Retrieved OASys RBAC permissions for oasys user $oasysUserCode, area $area, offender $offenderPk, assessment $oasysSetPk and assessment type $assessmentType") }
  }

  private fun handlePermissionsError(
    oASysRBACPermissionsDto: OASysRBACPermissionsDto,
    clientResponse: ClientResponse,
    method: HttpMethod,
    url: String
  ): Mono<out Throwable?>? {
    return when (clientResponse.statusCode()) {
      HttpStatus.FORBIDDEN -> {
        log.error("Oasys returned Forbidden for $oASysRBACPermissionsDto")
        clientResponse.bodyToMono(OASysRBACErrorResponse::class.java)
          .map { error ->
            ExternalApiForbiddenException(
              msg = error.developerMessage ?: "",
              method = method,
              url = url,
              client = ExternalService.ASSESSMENTS_API,
              moreInfo = error.payload?.permissions?.get(0)?.returnMessage,
              reason = ExceptionReason.OASYS_PERMISSION
            )
          }
      }
      HttpStatus.BAD_REQUEST -> {
        log.error("Oasys returned bad request for $oASysRBACPermissionsDto")
        clientResponse.bodyToMono(OASysRBACErrorResponse::class.java)
          .map { error ->
            ExternalApiInvalidRequestException(
              error.developerMessage ?: "",
              method, url, ExternalService.ASSESSMENTS_API
            )
          }
      }
      else -> {
        handleError(clientResponse, method, url, ExternalService.ASSESSMENTS_API)
      }
    }
  }

  fun handleAssessmentError(
    oasysSetPk: Long?,
    clientResponse: ClientResponse,
    method: HttpMethod,
    url: String
  ): Mono<out Throwable?>? {
    return when (clientResponse.statusCode()) {
      HttpStatus.NOT_FOUND -> {
        log.error("Oasys assessment $oasysSetPk not found")
        clientResponse.bodyToMono(OASysErrorResponse::class.java)
          .map { error ->
            ExternalApiEntityNotFoundException(
              error.developerMessage?.let { error.developerMessage } ?: "",
              method, url, ExternalService.ASSESSMENTS_API
            )
          }
      }
      else -> handleError(clientResponse, method, url, ExternalService.ASSESSMENTS_API)
    }
  }
}
