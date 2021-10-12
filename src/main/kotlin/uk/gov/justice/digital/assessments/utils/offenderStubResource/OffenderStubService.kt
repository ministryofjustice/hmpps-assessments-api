package uk.gov.justice.digital.assessments.utils.offenderStubResource

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.services.OffenderService
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

const val AREA_CODE = "WWS"
const val EVENT_ID = 1L

@Service
@Profile("dev", "test")
class OffenderStubService(
  val assessmentApiRestClient: AssessmentApiRestClient,
  val communityApiRestClient: CommunityApiRestClient,
  val offenderService: OffenderService,
  val assessmentUpdateRestClient: AssessmentUpdateRestClient,
  @Value("\${stub.restricted}") val restrictedCrns: String = "",
) {
  companion object {
    const val PAGE_SIZE = 100
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }
  fun createStub(): OffenderAndOffenceStubDto {

    val existingStubs = assessmentApiRestClient.getOffenderStubs()
    val stubsSize = existingStubs.size
    log.info("Found $stubsSize existing offender stubs")
    val communityOffenders = communityApiRestClient.getPrimaryIds(stubsSize.div(PAGE_SIZE), PAGE_SIZE)
    val unusedId = communityOffenders?.firstOrNull { !checkForUsedCrn(it.crn, existingStubs) && !checkForRestrictedCrn(it.crn) }
      ?: throw EntityNotFoundException("Could not get unused CRN from Community API.")
    val offenceDetail = offenderService.getOffence(unusedId.crn, EVENT_ID)
    val newOffenderStubDto = generateOffenderStubDto(unusedId.crn)
    assessmentUpdateRestClient.createOasysOffenderStub(newOffenderStubDto)
    return OffenderAndOffenceStubDto.from(newOffenderStubDto, offenceDetail)
  }

  fun generateOffenderStubDto(crn: String): OffenderStubDto {
    val offender = communityApiRestClient.getOffender(crn)
    return OffenderStubDto(
      crn = crn,
      pnc = offender?.otherIds?.pncNumber,
      forename1 = offender?.firstName,
      familyName = offender?.surname,
      dateOfBirth = offender?.dateOfBirth,
      gender = offender?.gender,
      areaCode = AREA_CODE
    )
  }

  fun checkForUsedCrn(crn: String?, existingStubs: List<OffenderStubDto>): Boolean {
    return existingStubs.any { it.crn == crn }
  }

  fun checkForRestrictedCrn(crn: String?): Boolean {
    val restricted = restrictedCrns.split(',')
    return restricted.any { it == crn }
  }
}

data class PrimaryId(
  val crn: String
)

data class OffendersPage(
  val totalElements: Long,
  val numberOfElements: Long,
  val content: List<PrimaryId>
)
