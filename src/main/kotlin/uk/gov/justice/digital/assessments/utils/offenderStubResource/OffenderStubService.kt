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
import java.time.LocalDate

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
  @Value("\${stub.offset}") val offset: Int = 0,
) {
  companion object {
    const val PAGE_SIZE = 100
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun createOffenderAndOffenceStub(): OffenderAndOffenceStubDto {
    val existingStubs = assessmentApiRestClient.getOffenderStubs()
    log.info("Found ${existingStubs.size} existing offender stubs")
    val unusedId = findUnusedCrn(existingStubs)
    return createOffenderAndOffenceStub(unusedId.crn)
  }

  private fun findUnusedCrn(existingStubs: List<OffenderStubDto>): PrimaryId {
    val stubsSize = existingStubs.size
    val pageNumber = (stubsSize + offset).div(PAGE_SIZE)
    val communityOffenders = communityApiRestClient.getPrimaryIds(pageNumber, PAGE_SIZE)
    return communityOffenders?.firstOrNull { !checkForUsedCrn(it.crn, existingStubs) && !checkForRestrictedCrn(it.crn) }
      ?: throw EntityNotFoundException("Could not find unused CRN from Community API page $pageNumber with page size: $PAGE_SIZE.")
  }

  fun checkForUsedCrn(crn: String?, existingStubs: List<OffenderStubDto>): Boolean {
    return existingStubs.any { it.crn == crn }
  }

  fun checkForRestrictedCrn(crn: String?): Boolean {
    val restricted = restrictedCrns.split(',')
    return restricted.any { it == crn }
  }

  fun createOffenderAndOffenceStub(crn: String): OffenderAndOffenceStubDto {
    val offenceDetail = offenderService.getOffenceFromConvictionIndex(crn, EVENT_ID)
    val newOffenderStubDto = createOffenderStubDto(crn)
    return OffenderAndOffenceStubDto.from(newOffenderStubDto, offenceDetail)
  }

  fun createOffenderStubDto(crn: String): OffenderStubDto {
    val offender = communityApiRestClient.getOffender(crn)
    return OffenderStubDto(
      crn = crn,
      pnc = offender?.otherIds?.pncNumber,
      forename1 = offender?.firstName,
      familyName = offender?.surname,
      dateOfBirth = offender?.dateOfBirth.let { LocalDate.parse(offender?.dateOfBirth) },
      gender = offender?.gender,
      areaCode = AREA_CODE
    )
  }

  fun createStubFromCrn(crn: String): OffenderAndOffenceStubDto {
    val existingStubs = assessmentApiRestClient.getOffenderStubs()
    if (checkForUsedCrn(crn, existingStubs)) {
      throw StubAlreadyExistsException("A stub already exists for CRN: $crn")
    }
    return createOffenderAndOffenceStub(crn)
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

class StubAlreadyExistsException(msg: String?) : RuntimeException(msg)
