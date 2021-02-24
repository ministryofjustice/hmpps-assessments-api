package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.Offence
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenceDetail
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class OffenderServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val offenderService: OffenderService = OffenderService(communityApiRestClient)

  private val oasysOffenderPk = 101L
  private val crn = "DX12340A"
  private val convictionId = 636401162L

  @Test
  fun `should return offender if one exists`() {
    every { communityApiRestClient.getOffender(crn) } returns validOffenderDto()

    val offenderDto = offenderService.getOffender(crn)
    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  @Test
  fun `should return offence if one exists`() {
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validOffenceDto()

    val offenceDto = offenderService.getOffence(crn, convictionId)
    assertThat(offenceDto.convictionId).isEqualTo(convictionId)
    assertThat(offenceDto.mainOffenceId).isEqualTo("offence1")
    assertThat(offenceDto.offenceCode).isEqualTo("code1")

    verify(exactly = 1) { communityApiRestClient.getConviction(any(), any()) }
  }

  @Test
  fun `should return offender and offence if they exist`() {
    every { communityApiRestClient.getOffender(crn) } returns validOffenderDto()
    every { communityApiRestClient.getConviction(crn, convictionId) } returns validOffenceDto()

    val offenderDto = offenderService.getOffenderAndOffence(crn, convictionId)

    assertThat(offenderDto.offenderId).isEqualTo(oasysOffenderPk)
    assertThat(offenderDto.offence?.convictionId).isEqualTo(convictionId)
    assertThat(offenderDto.offence?.mainOffenceId).isEqualTo("offence1")
    assertThat(offenderDto.offence?.offenceCode).isEqualTo("code1")
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
    verify(exactly = 1) { communityApiRestClient.getConviction(any(), any()) }
  }

  private fun validOffenceDto(): CommunityConvictionDto {
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

  private fun validOffenderDto(): CommunityOffenderDto {
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
      )
    )
  }
}
