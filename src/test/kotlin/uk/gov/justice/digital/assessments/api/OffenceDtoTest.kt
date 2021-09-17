package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDetail
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDto
import uk.gov.justice.digital.assessments.restclient.communityapi.Sentence
import java.time.LocalDate

@DisplayName("Offence DTO Tests")
class OffenceDtoTest {

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
        sentence = Sentence(startDate = LocalDate.of(2020, 2, 1)),
        index = 1
    )

    val offenceDto = OffenceDto.from(communityConvictionDto)

    assertThat(offenceDto.offenceCode).isEqualTo("main category code")
    assertThat(offenceDto.codeDescription).isEqualTo("code description 1")
    assertThat(offenceDto.offenceSubCode).isEqualTo("subcategory code")
    assertThat(offenceDto.subCodeDescription).isEqualTo("code description 2")
  }
}
