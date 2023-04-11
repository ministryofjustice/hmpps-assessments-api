package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Address
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Alias
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.MainOffence
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Sentence
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Type
import java.time.LocalDate

@DisplayName("Offender DTO Tests")
class OffenderDtoTest {

  @Test
  fun `builds valid offender DTO from Community Offender`() {
    val caseDetails = CaseDetails(
      crn = "crn",
      name = Name(
        forename = "forename",
        middleName = "middleName",
        surname = "surname",
      ),
      dateOfBirth = LocalDate.of(1989, 1, 1),
      genderIdentity = "PREFER TO SELF DESCRIBE",
      aliases = listOf(
        Alias(
          name = Name(
            forename = "firstName",
            surname = "surname",
          ),
          dateOfBirth = LocalDate.of(1988, 1, 2),
        ),
        Alias(
          name = Name(
            forename = "firstName2",
            surname = "surname2",
          ),
          dateOfBirth = LocalDate.of(1988, 1, 2),
        ),
      ),
      mainAddress = Address(
        buildingName = "HMPPS Digital Studio",
        addressNumber = "32",
        district = "Sheffield City Centre",
        county = "South Yorkshire",
        postcode = "S3 7BS",
        town = "Sheffield",
      ),
      sentence = Sentence(
        startDate = LocalDate.of(2020, 2, 1),
        mainOffence = MainOffence(
          category = Type(
            code = "Code",
            description = "Code description",
          ),
          subCategory = Type(
            code = "Sub code",
            description = "Sub code description",
          ),
        ),
      ),
    )

    val offenderDto = OffenderDto.from(caseDetails, 1)

    assertThat(offenderDto.firstName).isEqualTo(caseDetails.name.forename)
    assertThat(offenderDto.surname).isEqualTo(caseDetails.name.surname)
    assertThat(offenderDto.dateOfBirth).isEqualTo(caseDetails.dateOfBirth)
    assertThat(offenderDto.gender).isEqualTo(caseDetails.gender)
    assertThat(offenderDto.crn).isEqualTo(caseDetails.crn)
    assertThat(offenderDto.pncNumber).isEqualTo(caseDetails.pncNumber)
    assertThat(offenderDto.croNumber).isEqualTo(caseDetails.croNumber)
    assertThat(offenderDto.firstNameAliases).containsExactly("firstName", "firstName2")
    assertThat(offenderDto.surnameAliases).containsExactly("surname", "surname2")
  }
}
