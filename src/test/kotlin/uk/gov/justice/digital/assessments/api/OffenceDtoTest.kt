package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.Offence
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenceDetail
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.DefendantAddress
import java.time.LocalDate

@DisplayName("Offender DTO Tests")
class OffenceDtoTest {

  private val convictionDate: LocalDate = LocalDate.now()
  @Test
  fun `builds valid offender DTO from Community Offender`() {
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

  @Test
  fun `builds valid Address from Defendant address`() {
    val defendantAddress = DefendantAddress(
      line1 = "line1",
      line2 = "line2",
      line3 = "line3",
      line4 = "line4",
      line5 = "line5",
      postcode = "postcode"
    )

    val address = Address.from(defendantAddress)

    assertThat(address?.address1).isEqualTo(defendantAddress.line1)
    assertThat(address?.address2).isEqualTo(defendantAddress.line2)
    assertThat(address?.address3).isEqualTo(defendantAddress.line3)
    assertThat(address?.address4).isEqualTo(defendantAddress.line4)
    assertThat(address?.address5).isEqualTo(defendantAddress.line5)
    assertThat(address?.postcode).isEqualTo(defendantAddress.postcode)
  }
}
