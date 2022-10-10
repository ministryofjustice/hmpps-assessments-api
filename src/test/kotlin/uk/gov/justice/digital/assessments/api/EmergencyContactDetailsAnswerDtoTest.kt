package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
import uk.gov.justice.digital.assessments.restclient.communityapi.PersonalContact

class EmergencyContactDetailsAnswerDtoTest {

  @Test
  fun `should map empty delius values to empty list values in dto`() {

    // Given
    val personalContact = createEmptyPersonalContact()

    // When
    val emergencyContactDetailsAnswerDto = EmergencyContactDetailsAnswerDto.from(personalContact)

    // Then
    assertThat(emergencyContactDetailsAnswerDto.firstName).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.familyName).isEmpty()
    assertThat(emergencyContactDetailsAnswerDto.relationship).isEmpty()
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
      personalContactId = null,
      relationship = null,
      startDate = null,
      endDate = null,
      title = null,
      firstName = null,
      otherNames = null,
      surname = null,
      previousSurname = null,
      mobileNumber = null,
      emailAddress = null,
      notes = null,
      gender = null,
      relationshipType = null,
      createdDatetime = null,
      lastUpdatedDatetime = null,
      address = null,
      isActive = null
    )
  }
}
