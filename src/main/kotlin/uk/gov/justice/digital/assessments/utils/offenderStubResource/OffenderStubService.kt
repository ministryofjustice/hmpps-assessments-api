package uk.gov.justice.digital.assessments.utils.offenderStubResource

import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Page
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient.Companion.log
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.restclient.handle4xxError
import uk.gov.justice.digital.assessments.restclient.handle5xxError
import javax.persistence.EntityNotFoundException

@Service
class OffenderStubService(
  val assessmentApiRestClient: AssessmentApiRestClient,
  val communityApiRestClient: CommunityApiRestClient
  ) {

  fun createStub(): Any? {

    val existingStubs = assessmentApiRestClient.getOffenderStubs() ?: emptyList()
    val offset = existingStubs.size
    val communityOffenders = communityApiRestClient.getOffenders(offset.div(10))
    communityOffenders[offset.rem(10).minus(1)]
    return null
    //TODO work in progress
  }

  fun CommunityApiRestClient.getOffenders(page: Int): MutableList<PrimaryIdentifiers> {
    val queryParameters = "/offenders/primaryIdentifiers/?includeActiveOnly=active&sort=offenderId,asc&page=$page"
    val offendersPage = webClient
      .get(queryParameters)
      .retrieve()
      .bodyToMono(object: ParameterizedTypeReference<Page<PrimaryIdentifiers>>() {} )
      .block()
      ?: throw EntityNotFoundException("No offenders returned from Delius")
    log.info("Retrieved ${offendersPage.size} offender stubs")
    return offendersPage.content
  }

  fun AssessmentApiRestClient.getOffenderStubs(): List<OffenderStubDto>? {
    val url = "/offender/stub"
    return webClient
      .get(url)
      .retrieve()
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to retrieve OASys offender stubs",
          HttpMethod.POST,
          url,
          ExternalService.ASSESSMENTS_API
        )
      }
      .bodyToMono(object: ParameterizedTypeReference<List<OffenderStubDto>>() {})
      .block()
      .also { AssessmentApiRestClient.log.info("Retrieved offender stubs") }
  }

  fun AssessmentUpdateRestClient.createOasysOffenderStub(offenderStubDto: OffenderStubDto
  ): Long? {
    AssessmentUpdateRestClient.log.info("Creating offender stub in OASys for crn: ${offenderStubDto.crn}")
    val path = "/offender/stub"
    return webClient
      .post(path, offenderStubDto)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.POST, path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to create offender stub ${offenderStubDto.crn} in OASYs",
          HttpMethod.POST, path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(Long::class.java)
      .block()?.also {
        AssessmentUpdateRestClient.log.info("Created offender stub in OASys for crn: ${offenderStubDto.crn}")
      }
  }

  class PrimaryIdentifiers(
    private val offenderId: Long? = null,
    private val crn: String? = null
  )
}