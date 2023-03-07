package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact

class EmergencyContactDetailsAnswerDtoTest {

  @Test
  fun `should map empty delius values to empty list values in dto`() {

    // Given
    val personalContact = createEmptyPersonalContact()

    // When
    val emergencyContactDetailsAnswerDto = EmergencyContactDetailsAnswerDto.from(personalContact)

    // Then
    assertThat(emergencyContactDetailsAnswerDto.buildingName).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.addressNumber).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.streetName).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.district).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.town).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.county).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.postcode).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.telephoneNumber).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.mobileNumber).isEmpty()
  }

  private fun createEmptyPersonalContact(): PersonalContact {
    return PersonalContact(
      relationship = "",
      name = Name(
        forename = "",
        middleName = null,
        surname = ""
      ),
      telephoneNumber = null,
      mobileNumber = null,
      address = null
    )
  }
}
