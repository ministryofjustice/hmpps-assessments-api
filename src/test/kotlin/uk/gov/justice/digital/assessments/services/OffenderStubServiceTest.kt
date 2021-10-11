package uk.gov.justice.digital.assessments.services

import io.mockk.Called
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.OffenceDto
import uk.gov.justice.digital.assessments.restclient.AssessmentApiRestClient
import uk.gov.justice.digital.assessments.restclient.AssessmentUpdateRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderAndOffenceStubDto
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderStubDto
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderStubService
import uk.gov.justice.digital.assessments.utils.offenderStubResource.PrimaryId
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("Offender Stub Service Tests")
class OffenderStubServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val assessmentUpdateRestClient: AssessmentUpdateRestClient = mockk()
  private val assessmentApiRestClient: AssessmentApiRestClient = mockk()
  private val offenderService: OffenderService = mockk()
  private val offenderStubService = OffenderStubService(assessmentApiRestClient, communityApiRestClient, offenderService, assessmentUpdateRestClient, "D001055,D001056")

  @Test
  fun `return offender and offence codes`() {
    val crn = "D001057"
    every { assessmentApiRestClient.getOffenderStubs() } returns offenderStubs()
    every { communityApiRestClient.getPrimaryIds(0, 200) } returns primaryIdentifiers()
    every { communityApiRestClient.getOffender(crn) } returns communityOffenderDto()
    every { offenderService.getOffence(crn, 1) } returns offenceDto()
    justRun { assessmentUpdateRestClient.createOasysOffenderStub(any()) }

    val offenderOffenceDetails = offenderStubService.createStub()

    verify(exactly = 1) {
      assessmentUpdateRestClient.createOasysOffenderStub(
        OffenderStubDto(
          crn = crn,
          pnc = "A/1234560BA",
          familyName = "Smith",
          forename1 = "John",
          gender = "F",
          dateOfBirth = LocalDate.of(1979, 8, 18),
          areaCode = "WWS"
        )
      )
    }
    assertThat(offenderOffenceDetails).isEqualTo(
      OffenderAndOffenceStubDto(
        crn = crn,
        pnc = "A/1234560BA",
        familyName = "Smith",
        forename1 = "John",
        dateOfBirth = LocalDate.of(1979, 8, 18),
        gender = "F",
        areaCode = "WWS",
        offenceCode = "046",
        codeDescription = "Stealing from shops and stalls (shoplifting)",
        offenceSubCode = "00",
        subCodeDescription = "Stealing from shops and stalls (shoplifting)",
        sentenceDate = LocalDate.of(2014, 8, 25)
      )
    )
  }

  @Test
  fun `return offender throws exception for no unused CRNs`() {
    every { assessmentApiRestClient.getOffenderStubs() } returns allOffenderStubs()
    every { communityApiRestClient.getPrimaryIds(0, 200) } returns primaryIdentifiers()

    assertThrows<EntityNotFoundException> { offenderStubService.createStub() }

    verify { assessmentUpdateRestClient wasNot Called }
    verify(exactly = 0) { communityApiRestClient.getOffender(any()) }
  }

  private fun offenderStubs(): List<OffenderStubDto> {
    return listOf(
      OffenderStubDto(
        crn = "D001022",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
      OffenderStubDto(
        crn = "D001040",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
    )
  }

  private fun allOffenderStubs(): List<OffenderStubDto> {
    return listOf(
      OffenderStubDto(
        crn = "D001022",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
      OffenderStubDto(
        crn = "D001040",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
      OffenderStubDto(
        crn = "D001055",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
      OffenderStubDto(
        crn = "D001056",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
      OffenderStubDto(
        crn = "D001057",
        pnc = "12345",
        familyName = "familyname",
        forename1 = "forename",
        gender = "Male",
        dateOfBirth = LocalDate.of(2000, 1, 1),
        areaCode = "WWS"
      ),
    )
  }

  private fun primaryIdentifiers(): List<PrimaryId> {
    return listOf(
      PrimaryId(
        crn = "D001022"
      ),
      PrimaryId(
        crn = "D001040"
      ),
      PrimaryId(
        crn = "D001057"
      )
    )
  }

  private fun communityOffenderDto(): CommunityOffenderDto {
    return CommunityOffenderDto(
      offenderId = 101L,
      firstName = "John",
      middleNames = null,
      surname = "Smith",
      previousSurname = null,
      dateOfBirth = LocalDate.of(1979, 8, 18),
      gender = "F",
      otherIds = IDs(
        crn = "D001057",
        pncNumber = "A/1234560BA"
      )
    )
  }

  private fun offenceDto(): OffenceDto {
    return OffenceDto(
      offenceCode = "046",
      codeDescription = "Stealing from shops and stalls (shoplifting)",
      offenceSubCode = "00",
      subCodeDescription = "Stealing from shops and stalls (shoplifting)",
      sentenceDate = LocalDate.of(2014, 8, 25)
    )
  }
}
