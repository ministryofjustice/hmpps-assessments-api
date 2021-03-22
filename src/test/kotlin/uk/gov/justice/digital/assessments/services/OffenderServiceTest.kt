package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import uk.gov.justice.digital.assessments.jpa.repositories.SubjectRepository
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.CourtCaseRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.Offence
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenceDetail
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.DefendantAddress
import uk.gov.justice.digital.assessments.services.exceptions.EntityNotFoundException
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("Offender Service Tests")
class OffenderServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val courtCaseRestClient: CourtCaseRestClient = mockk()
  private val subjectRepository: SubjectRepository = mockk()
  private val offenderService: OffenderService = OffenderService(communityApiRestClient, subjectRepository, courtCaseRestClient)

  private val oasysOffenderPk = 101L
  private val crn = "DX12340A"
  private val convictionId = 636401162L

  @Test
  fun `should return offender if one exists`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()

    val offenderDto = offenderService.getOffender(crn)
    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  @Test
  fun `should return offence if one exists`() {
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validCommunityConvictionDto()

    val offenceDto = offenderService.getOffence(crn, convictionId)
    assertThat(offenceDto.convictionId).isEqualTo(convictionId)
    assertThat(offenceDto.mainOffenceId).isEqualTo("offence1")
    assertThat(offenceDto.offenceCode).isEqualTo("code1")

    verify(exactly = 1) { communityApiRestClient.getConviction(any(), any()) }
  }

  @Test
  fun `should return offender and offence if they exist`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validCommunityConvictionDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, convictionId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.offence?.convictionId).isEqualTo(convictionId)
    assertThat(offenderDto.offence?.mainOffenceId).isEqualTo("offence1")
    assertThat(offenderDto.offence?.offenceCode).isEqualTo("code1")
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConviction(any(), any()) }
  }

  @Test
  fun `should return offender with address if it exists`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validCommunityConvictionDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, convictionId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.address?.address1).isEqualTo("line1")
    assertThat(offenderDto.address?.postcode).isEqualTo("postcode")

    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConviction(any(), any()) }
  }

  @Test
  fun `throws exceptions when no offender exists for CRN`() {
    every { communityApiRestClient.getOffender(crn) } returns null

    assertThrows<EntityNotFoundException> { offenderService.getOffenderAndOffence(crn, convictionId) }
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  @Test
  fun `should return court code and case number when they exist`() {
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
  fun `should return offender address when it exists`() {
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()
    val address = offenderService.getOffenderAddress(crn)

    assertThat(address?.address1).isEqualTo("line1")
    assertThat(address?.postcode).isEqualTo("postcode")
    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
    verify(exactly = 1) { courtCaseRestClient.getCourtCase(any(), any()) }
  }

  @Test
  fun `should return null offender address when none exist`() {
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns CourtCase()
    val address = offenderService.getOffenderAddress(crn)

    assertThat(address).isNull()

    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
    verify(exactly = 1) { courtCaseRestClient.getCourtCase(any(), any()) }
  }

  @Test
  fun `should return offender with alias if it exists`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validCommunityConvictionDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, convictionId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.firstNameAliases?.get(0)).isEqualTo("firstName")
    assertThat(offenderDto.surnameAliases?.get(0)).isEqualTo("surname")

    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConviction(any(), any()) }
  }

  @Test
  fun `should return empty offender aliases list when none exist`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto().copy(offenderAliases = emptyList())
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validCommunityConvictionDto()
    every { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(crn, "COURT") } returns validCourtSubject()
    every { courtCaseRestClient.getCourtCase("courtCode", "caseNumber") } returns validCourtCase()

    val offenderDto = offenderService.getOffenderAndOffence(crn, convictionId)

    assertThat(offenderDto.firstNameAliases).isEmpty()
    assertThat(offenderDto.surnameAliases).isEmpty()
    verify(exactly = 1) { subjectRepository.findAllByCrnAndSourceOrderByCreatedDateDesc(any(), "COURT") }
    verify(exactly = 1) { courtCaseRestClient.getCourtCase(any(), any()) }
  }

  private fun validCourtSubject(): List<SubjectEntity> {
    return listOf(SubjectEntity(sourceId = "courtCode|caseNumber"))
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
      )
    )
  }

  private fun validCommunityConvictionDto(): CommunityConvictionDto {
    return CommunityConvictionDto(
      convictionId = 636401162L,
      offences = listOf(
        Offence(
          offenceId = "offence1",
          mainOffence = true,
          detail = OffenceDetail(
            code = "code1",
            description = "Offence description"
          )
        ),
        Offence(
          offenceId = "offence2",
          mainOffence = false,
          detail = OffenceDetail(
            code = "code2",
            description = "Offence description"
          )
        )
      ),
      convictionDate = LocalDate.of(2020, 2, 1)
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
