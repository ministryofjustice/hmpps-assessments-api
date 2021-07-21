package uk.gov.justice.digital.assessments.utils.offenderStubResource

import org.springframework.core.ParameterizedTypeReference
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
  val communityApiRestClient: CommunityApiRestClient,
  val assessmentUpdateRestClient: AssessmentUpdateRestClient
) {

  fun createStub(offenderStubDto: OffenderStubDto): String {

    val existingStubs = assessmentApiRestClient.getOffenderStubs()
    val stubsSize = existingStubs.size
    val callsToCommunityApi = 0
    var validCrn: String?
    do {
      val communityOffenders = communityApiRestClient.getOffenders(stubsSize.div(10))
      validCrn = communityOffenders?.first { !checkForUsedCrn(it.crn, existingStubs) }?.crn
      stubsSize.plus(10)
    } while (callsToCommunityApi <= 3 && validCrn.isNullOrEmpty())

    if (validCrn.isNullOrEmpty()) {
      throw EntityNotFoundException("Could not get unused CRN from Community API.")
    }
    val crnOffenderStubDto = generateOffenderStubDto(validCrn, offenderStubDto)
    assessmentUpdateRestClient.createOasysOffenderStub(crnOffenderStubDto)
    return validCrn
  }

  fun generateOffenderStubDto(validCrn: String?, offenderStubDto: OffenderStubDto): OffenderStubDto {
    return offenderStubDto.copy(crn = validCrn)
  }

  fun checkForUsedCrn(crn: String?, existingStubs: List<OffenderStubDto>): Boolean {
    return existingStubs.any { it.crn == crn }
  }

  fun CommunityApiRestClient.getOffenders(page: Int): List<PrimaryIdentifiers>? {
    val path = "/offender/stub"
    val offendersPage = webClient
      .get(path)
      .retrieve()
      .bodyToMono(OffendersPage::class.java)
      .block()

    log.info("Retrieved ${offendersPage?.content?.size} offender stubs")
    return offendersPage?.content ?: throw EntityNotFoundException("Failed to retrieve CRNs from Community API")
  }

  fun AssessmentApiRestClient.getOffenderStubs(): List<OffenderStubDto> {
    val url = "/offender/stub"
    val offenderStubs = webClient
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
      .bodyToMono(object : ParameterizedTypeReference<List<OffenderStubDto>>() {})
      .block()
      .also { AssessmentApiRestClient.log.info("Retrieved offender stubs") }
    return offenderStubs ?: throw EntityNotFoundException("Failed to retrieve OASys offender stubs")
  }

  private fun AssessmentUpdateRestClient.createOasysOffenderStub(
    offenderStubDto: OffenderStubDto
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

  data class PrimaryIdentifiers(val crn: String? = null)
  data class OffendersPage(val totalElements: Long, val numberOfElements: Long, val content: List<PrimaryIdentifiers>)
}
