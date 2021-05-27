package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.Offence
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenceDetail
import java.time.LocalDate

@DisplayName("Offence DTO Tests")
class OffenceDtoTest {

  private val convictionDate: LocalDate = LocalDate.now()
  @Test
  fun `builds offence DTO from Community Conviction`() {
    val communityConvictionDto = CommunityConvictionDto(
      convictionId = 1234L,
      offences = listOf(
        Offence(
          offenceId = "offenceId",
          mainOffence = true,
          detail = OffenceDetail(
            code = "offence code",
            description = "offence description",
            mainCategoryCode = "main category code",
            mainCategoryDescription = "code description 1",
            subCategoryCode = "subcategory code",
            subCategoryDescription = "code description 2"
          )
        )
      ),
      convictionDate = convictionDate
    )

    val offenceDto = OffenceDto.from(communityConvictionDto)

    assertThat(offenceDto.convictionId).isEqualTo(1234L)
    assertThat(offenceDto.convictionDate).isEqualTo(convictionDate)
    assertThat(offenceDto.mainOffenceId).isEqualTo("offenceId")
    assertThat(offenceDto.offenceCode).isEqualTo("offence code")
    assertThat(offenceDto.offenceDescription).isEqualTo("offence description")
    assertThat(offenceDto.categoryCode).isEqualTo("main category code")
    assertThat(offenceDto.categoryDescription).isEqualTo("code description 1")
    assertThat(offenceDto.subCategoryCode).isEqualTo("subcategory code")
    assertThat(offenceDto.subCategoryDescription).isEqualTo("code description 2")
  }
}
