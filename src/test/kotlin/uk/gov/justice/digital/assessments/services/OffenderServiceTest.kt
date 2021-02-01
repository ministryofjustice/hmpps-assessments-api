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
import uk.gov.justice.digital.assessments.restclient.communityapi.GetOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("Assessment Service Tests")
class OffenderServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val offenderService: OffenderService = OffenderService(communityApiRestClient)

  private val oasysOffenderPk = 101L
  private val crn = "DX12340A"

  @Test
  fun `should return existing assessment if one exists`() {
    every { communityApiRestClient.getOffender(crn) } returns validGetOffenderDto()

    val offenderDto = offenderService.getOffender(crn)
    assertThat(offenderDto?.offenderId).isEqualTo(oasysOffenderPk)
    verify(exactly = 1) { communityApiRestClient.getOffender(any()) }
  }

  private fun validGetOffenderDto(): GetOffenderDto {
    return GetOffenderDto(
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
