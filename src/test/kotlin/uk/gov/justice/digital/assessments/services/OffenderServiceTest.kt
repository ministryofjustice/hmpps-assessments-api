package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpMethod
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.ExternalService
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDetail
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.DefendantAddress
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException

@ExtendWith(MockKExtension::class)
@DisplayName("Offender Service Tests")
class OffenderServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val offenderService: OffenderService =
    OffenderService(communityApiRestClient, subjectRepository, courtCaseRestClient)

  private val oasysOffenderPk = 101L
  private val crn = "DX12340A"
  private val eventId = 1L

  @Test
  fun `return offender`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()

    val offenderDto = offenderService.getOffender(crn)
    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  @Test
  fun `return offence`() {
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()

    val offenceDto = offenderService.getOffence(crn, eventId)
    assertThat(offenceDto.convictionId).isEqualTo(636401162)
    assertThat(offenceDto.offenceCode).isEqualTo("Code")
    assertThat(offenceDto.codeDescription).isEqualTo("Code description")
    assertThat(offenceDto.offenceSubCode).isEqualTo("Sub code")
    assertThat(offenceDto.subCodeDescription).isEqualTo("Sub code description")

    verify(exactly = 1) { communityApiRestClient.getConvictions(any()) }
  }

  @Test
  fun `return offender and offence`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, eventId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.offence?.convictionId).isEqualTo(636401162)
    assertThat(offenderDto.offence?.offenceCode).isEqualTo("Code")
    assertThat(offenderDto.offence?.codeDescription).isEqualTo("Code description")
    assertThat(offenderDto.offence?.offenceSubCode).isEqualTo("Sub code")
    assertThat(offenderDto.offence?.subCodeDescription).isEqualTo("Sub code description")
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConvictions(any()) }
  }

  @Test
  fun `return offender with address`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, eventId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.address?.address1).isEqualTo("line1")
    assertThat(offenderDto.address?.postcode).isEqualTo("postcode")

    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConvictions(any()) }
  }

  @Test
  fun `throws exceptions when no offender exists for CRN`() {
    every { communityApiRestClient.getOffender(crn) } throws ExternalApiEntityNotFoundException(
      "",
      HttpMethod.GET,
      "secure/offenders/crn/$crn/all",
      ExternalService.COMMUNITY_API
    )

    assertThrows<ExternalApiEntityNotFoundException> { offenderService.getOffenderAndOffence(crn, eventId) }
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  @Test
  fun `return court code and case number`() {
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()

    val courtSubject = offenderService.getCourtSubjectByCrn(crn)

    assertThat(courtSubject?.first).isEqualTo("courtCode")
    assertThat(courtSubject?.second).isEqualTo("caseNumber")
    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
  }

  @Test
  fun `returns null when no court subjects exist for CRN`() {
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns emptyList()

    val courtSubject = offenderService.getCourtSubjectByCrn(crn)
    assertThat(courtSubject).isNull()

    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
  }

  @Test
  fun `return offender address`() {
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()
    val address = offenderService.getOffenderAddress(crn)

    assertThat(address?.address1).isEqualTo("line1")
    assertThat(address?.postcode).isEqualTo("postcode")
    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
    verify(exactly = 1) { courtCaseRestClient.getCourtCase(any(), any()) }
  }

  @Test
  fun `return null offender address when none exist`() {
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns CourtCase(
      defendantDob = LocalDate.of(1989, 1, 1)
    )
    val address = offenderService.getOffenderAddress(crn)

    assertThat(address).isNull()

    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
    verify(exactly = 1) { courtCaseRestClient.getCourtCase(any(), any()) }
  }

  @Test
  fun `return offender with alias`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, eventId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.firstNameAliases?.get(0)).isEqualTo("firstName")
    assertThat(offenderDto.surnameAliases?.get(0)).isEqualTo("surname")

    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConvictions(any()) }
  }

  @Test
  fun `return empty offender aliases list when none exist`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto().copy(offenderAliases = emptyList())
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, eventId)

    assertThat(offenderDto.firstNameAliases).isEmpty()
    assertThat(offenderDto.surnameAliases).isEmpty()
    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
    verify(exactly = 1) { courtCaseRestClient.getCourtCase(any(), any()) }
  }

  @Test
  fun `return offence with category codes and category descriptions`() {
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()

    val offenceDto = offenderService.getOffence(crn, eventId)
    assertThat(offenceDto.convictionId).isEqualTo(636401162L)
    assertThat(offenceDto.offenceCode).isEqualTo("Code")
    assertThat(offenceDto.codeDescription).isEqualTo("Code description")
    assertThat(offenceDto.offenceSubCode).isEqualTo("Sub code")
    assertThat(offenceDto.subCodeDescription).isEqualTo("Sub code description")

    verify(exactly = 1) { communityApiRestClient.getConvictions(any()) }
  }

  private fun validCourtSubject(): List<SubjectEntity> {
    return listOf(
      SubjectEntity(
        sourceId = "courtCode|caseNumber",
        dateOfBirth = LocalDate.of(1989, 1, 1),
        crn = "X1345"
      )
    )
  }

  private fun validCourtCase(): CourtCase {
    return CourtCase(
      defendantAddress = DefendantAddress(
        line1 = "line1",
        line2 = "line2",
        line3 = "line3",
        line4 = "line4",
        line5 = "line5",
        postcode = "postcode"
      ),
      defendantDob = LocalDate.of(1989, 1, 1)
    )
  }

  private fun validCommunityConvictionsDto(): List<CommunityConvictionDto> {
    return listOf(
      CommunityConvictionDto(
        convictionId = 636401162L,
        offences = listOf(
          CommunityOffenceDto(
            offenceId = "offence1",
            mainOffence = true,
            detail = CommunityOffenceDetail(
              mainCategoryCode = "Code",
              mainCategoryDescription = "Code description",
              subCategoryCode = "Sub code",
              subCategoryDescription = "Sub code description"
            )
          ),
          CommunityOffenceDto(
            offenceId = "offence2",
            mainOffence = false,
            detail = CommunityOffenceDetail(
              mainCategoryCode = "Code",
              mainCategoryDescription = "Code description",
              subCategoryCode = "Sub code",
              subCategoryDescription = "Sub code description"
            )
          )
        ),
        convictionDate = LocalDate.of(2020, 2, 1),
        index = 1
      ),
      CommunityConvictionDto(
        convictionId = 1234567,
        offences = listOf(
          CommunityOffenceDto(
            offenceId = "offenceA",
            mainOffence = true,
            detail = CommunityOffenceDetail(
              mainCategoryCode = "Code",
              mainCategoryDescription = "Code description",
              subCategoryCode = "Sub code",
              subCategoryDescription = "Sub code description"
            )
          ),
          CommunityOffenceDto(
            offenceId = "offenceB",
            mainOffence = false,
            detail = CommunityOffenceDetail(
              mainCategoryCode = "Code",
              mainCategoryDescription = "Code description",
              subCategoryCode = "Sub code",
              subCategoryDescription = "Sub code description"
            )
          )
        ),
        convictionDate = LocalDate.of(2020, 2, 1),
        index = 2
      )
    )
  }

  private fun validCommunityOffenderDto(): CommunityOffenderDto {
    return CommunityOffenderDto(
      offenderId = 101L,
      firstName = "John",
      middleNames = null,
      surname = "Smith",
      previousSurname = null,
      dateOfBirth = LocalDate.of(1979, 8, 18),
      gender = "F",
      otherIds = IDs(
        crn = "DX12340A",
        pncNumber = "A/1234560BA"
      ),
      offenderAliases = listOf(
        OffenderAlias(
          firstName = "firstName",
          surname = "surname"
        )
      )
    )
  }
}
