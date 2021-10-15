package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.DefendantAddress
import java.time.LocalDate

@DisplayName("Offender DTO Tests")
class OffenderDtoTest {

  @Test
  fun `builds valid offender DTO from Community Offender`() {
    val communityOffenderDto = CommunityOffenderDto(
      offenderId = 101L,
      firstName = "John",
      middleNames = listOf("firstMiddleName", "secondMiddleName"),
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
        ),
        OffenderAlias(
          firstName = "firstName2",
          surname = "surname2"
        )
      )
    )

    val offenderDto = OffenderDto.from(communityOffenderDto)

    assertThat(offenderDto.offenderId).isEqualTo(communityOffenderDto.offenderId)
    assertThat(offenderDto.firstName).isEqualTo(communityOffenderDto.firstName)
    assertThat(offenderDto.surname).isEqualTo(communityOffenderDto.surname)
    assertThat(offenderDto.dateOfBirth).isEqualTo(communityOffenderDto.dateOfBirth)
    assertThat(offenderDto.gender).isEqualTo(communityOffenderDto.gender)
    assertThat(offenderDto.crn).isEqualTo(communityOffenderDto.otherIds?.crn)
    assertThat(offenderDto.pncNumber).isEqualTo(communityOffenderDto.otherIds?.pncNumber)
    assertThat(offenderDto.croNumber).isEqualTo(communityOffenderDto.otherIds?.croNumber)
    assertThat(offenderDto.firstNameAliases).containsExactly("firstName", "firstName2")
    assertThat(offenderDto.surnameAliases).containsExactly("surname", "surname2")
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
