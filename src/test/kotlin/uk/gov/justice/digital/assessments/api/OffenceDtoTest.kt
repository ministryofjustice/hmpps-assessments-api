package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDetail
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDto
import java.time.LocalDate

@DisplayName("Offence DTO Tests")
class OffenceDtoTest {

  private val convictionDate: LocalDate = LocalDate.now()
  @Test
  fun `builds offence DTO from Community Conviction`() {
    val communityConvictionDto = CommunityConvictionDto(
      convictionId = 1234L,
      offences = listOf(
        CommunityOffenceDto(
          offenceId = "offenceId",
          mainOffence = true,
          detail = CommunityOffenceDetail(
            mainCategoryCode = "main category code",
            mainCategoryDescription = "code description 1",
            subCategoryCode = "subcategory code",
            subCategoryDescription = "code description 2"
          )
        )
      ),
      convictionDate = convictionDate,
      index = 1
    )

    val offenceDto = OffenceDto.from(communityConvictionDto)

    assertThat(offenceDto.convictionId).isEqualTo(1234L)
    assertThat(offenceDto.convictionDate).isEqualTo(convictionDate)
    assertThat(offenceDto.offenceCode).isEqualTo("main category code")
    assertThat(offenceDto.codeDescription).isEqualTo("code description 1")
    assertThat(offenceDto.offenceSubCode).isEqualTo("subcategory code")
    assertThat(offenceDto.subCodeDescription).isEqualTo("code description 2")
  }
}
