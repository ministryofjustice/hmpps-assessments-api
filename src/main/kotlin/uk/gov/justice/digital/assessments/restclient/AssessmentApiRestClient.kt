package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMapAdapter
import uk.gov.justice.digital.assessments.redis.UserDetailsRedisRepository
import uk.gov.justice.digital.assessments.restclient.assessmentapi.FilteredReferenceDataDto
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RefElementDto
import uk.gov.justice.digital.assessments.utils.RequestData
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderStubDto
import java.time.LocalDateTime
import java.util.Optional.ofNullable
import javax.persistence.EntityNotFoundException

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

  fun getOASysLatestAssessment(
    crn: String,
    status: List<String> = emptyList(),
    types: List<String> = emptyList(),
    cloneable: Boolean = false,
    cutoffDate: LocalDateTime? = null
  ): String? {
    log.info("Retrieving OASys latest assessment for CRN $crn")
    val path = "/assessments/latest/$crn"
    val queryParams = mutableMapOf(
      "status" to status,
      "types" to types,
      "cloneable" to listOf(cloneable.toString()),
    )
    ofNullable<LocalDateTime>(cutoffDate).ifPresent {
      date ->
      queryParams["cutoffDate"] = listOf(date.toString())
    }

    return webClient
      .get(path, MultiValueMapAdapter(queryParams))
      .retrieve()
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve OASys Latest Assessment for crn $crn, status $status, types $types, " +
            "cloneable $cloneable, cutoffDate $cutoffDate.",
          HttpMethod.GET,
          path,
          ExternalService.ASSESSMENTS_API
        )
      }
      .bodyToMono(String::class.java)
      .block().also {
        log.info(
          "Retrieved OASys Latest Assessment for crn $crn, status $status, types $types, " +
            "cloneable $cloneable, cutoffDate $cutoffDate."
        )
      }
  }

  private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

  fun getFilteredReferenceData(
    oasysSetPk: Long,
    offenderPk: Long?,
    oasysAssessmentType: String,
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
          oasysAssessmentType,
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

  fun getOffenderStubs(): List<OffenderStubDto> {
    log.info("Client retrieving offender stubs from OASys")
    val url = "/offender/stub"
    val offenderStubs = webClient
      .get(url)
      .retrieve()
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve OASys offender stubs",
          HttpMethod.GET,
          url,
          ExternalService.ASSESSMENTS_API
        )
      }
      .bodyToMono(object : ParameterizedTypeReference<List<OffenderStubDto>>() {})
      .block()
      .also { log.info("Retrieved offender stubs") }
    return offenderStubs ?: throw EntityNotFoundException("Failed to retrieve OASys offender stubs")
  }
}
