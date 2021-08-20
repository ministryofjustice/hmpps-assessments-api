package uk.gov.justice.digital.assessments.utils.offenderStubResource

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException

const val AREA_CODE = "WWS"

@Service
@Profile("dev", "test")
class OffenderStubService(
  val assessmentApiRestClient: AssessmentApiRestClient,
  val communityApiRestClient: CommunityApiRestClient,
  val assessmentUpdateRestClient: AssessmentUpdateRestClient
) {

  fun createStub(): OffenderStubDto {

    val existingStubs = assessmentApiRestClient.getOffenderStubs()
    val stubsSize = existingStubs.size
    val communityOffenders = communityApiRestClient.getPrimaryIds(stubsSize.div(100))
    val unusedId = communityOffenders?.firstOrNull { !checkForUsedCrn(it.crn, existingStubs) }
      ?: throw EntityNotFoundException("Could not get unused CRN from Community API.")
    val newOffenderStubDto = generateOffenderStubDto(unusedId.crn)
    assessmentUpdateRestClient.createOasysOffenderStub(newOffenderStubDto)
    return newOffenderStubDto
  }

  fun generateOffenderStubDto(crn: String): OffenderStubDto {
    val offender = communityApiRestClient.getOffender(crn)
    return OffenderStubDto(
      crn = crn,
      pnc = offender?.otherIds?.pncNumber,
      forename1 = offender?.firstName,
      familyName = offender?.surname,
      areaCode = AREA_CODE
    )
  }

  fun checkForUsedCrn(crn: String?, existingStubs: List<OffenderStubDto>): Boolean {
    return existingStubs.any { it.crn == crn }
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
