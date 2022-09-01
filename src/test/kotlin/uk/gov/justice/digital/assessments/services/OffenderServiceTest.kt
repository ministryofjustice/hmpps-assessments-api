package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDetail
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import uk.gov.justice.digital.assessments.restclient.communityapi.Sentence
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("Offender Service Tests")
class OffenderServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val offenderService: OffenderService = OffenderService(communityApiRestClient)

  private val oasysOffenderPk = 101L
  private val crn = "DX12340A"
  private val eventId = 1L
  private val convictionId = 123456L

  @Test
  fun `should invoke community service Api with conviction Id when Delius EventType is EventId`() {
    // given
    every { communityApiRestClient.getConviction(crn, eventId) } returns CommunityConvictionDto(
      index = 1L,
      convictionId = 23423L,
      sentence = Sentence(LocalDate.now()),
      offences = listOf(
        CommunityOffenceDto(
          offenceId = "234234",
          mainOffence = true,
          detail = CommunityOffenceDetail(
            mainCategoryCode = "code",
            mainCategoryDescription = "categoryDescription",
            subCategoryCode = "sub category code",
            subCategoryDescription = "sub category description"
          )
        )
      )
    )

    // when
    offenderService.getOffence(DeliusEventType.EVENT_ID, crn, eventId)

    // then
    verify() { communityApiRestClient.getConviction(crn, eventId) }
  }

  @Test
  fun `should invoke community service Api with conviction Id when Delius EventType is Event index`() {
    // given
    every { communityApiRestClient.getConvictions(crn) } returns listOf(
      CommunityConvictionDto(
        index = 1L,
        convictionId = 23423L,
        sentence = Sentence(LocalDate.now()),
        offences = listOf(
          CommunityOffenceDto(
            offenceId = "234234",
            mainOffence = true,
            detail = CommunityOffenceDetail(
              mainCategoryCode = "code",
              mainCategoryDescription = "categoryDescription",
              subCategoryCode = "sub category code",
              subCategoryDescription = "sub category description"
            )
          )
        )
      )
    )

    // when
    offenderService.getOffence(DeliusEventType.EVENT_INDEX, crn, eventId)

    // then
    verify() { communityApiRestClient.getConvictions(crn) }
  }

  @Test
  fun `return offender`() {
    every { communityApiRestClient.getOffender(crn) } returns validCommunityOffenderDto()

    val offenderDto = offenderService.getOffender(crn)
    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  @Test
  fun `return offence for convictions`() {
    every { communityApiRestClient.getConvictions(crn) } returns validCommunityConvictionsDto()

    val offenceDto = offenderService.getOffenceFromConvictionIndex(crn, eventId)
    assertThat(offenceDto.convictionId).isEqualTo(636401162L)
    assertThat(offenceDto.convictionIndex).isEqualTo(1)
    assertThat(offenceDto.offenceCode).isEqualTo("Code")
    assertThat(offenceDto.codeDescription).isEqualTo("Code description")
    assertThat(offenceDto.offenceSubCode).isEqualTo("Sub code")
    assertThat(offenceDto.subCodeDescription).isEqualTo("Sub code description")

    verify(exactly = 1) { communityApiRestClient.getConvictions(any()) }
  }

  @Test
  fun `return offence for conviction`() {
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validCommunityConvictionsDto()[0]

    val offenceDto = offenderService.getOffenceFromConvictionId(crn, convictionId)
    assertThat(offenceDto.convictionId).isEqualTo(636401162L)
    assertThat(offenceDto.convictionIndex).isEqualTo(1)
    assertThat(offenceDto.offenceCode).isEqualTo("Code")
    assertThat(offenceDto.codeDescription).isEqualTo("Code description")
    assertThat(offenceDto.offenceSubCode).isEqualTo("Sub code")
    assertThat(offenceDto.subCodeDescription).isEqualTo("Sub code description")

    verify(exactly = 1) { communityApiRestClient.getConviction(crn, convictionId) }
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
        sentence = Sentence(startDate = LocalDate.of(2020, 2, 1)),
        index = 1,
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
        sentence = Sentence(startDate = LocalDate.of(2020, 2, 1)),
        index = 2
      )
    )
  }

  private fun validCommunityOffenderDto(): CommunityOffenderDto {
    return CommunityOffenderDto(
      offenderId = 101L,
      firstName = "John",
      middleNames = listOf("firstMiddleName", "secondMiddleName"),
      surname = "Smith",
      previousSurname = null,
      dateOfBirth = "1979-08-18",
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
