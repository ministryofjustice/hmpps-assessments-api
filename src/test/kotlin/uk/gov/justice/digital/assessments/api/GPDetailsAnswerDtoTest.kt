package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.PersonalContact

class GPDetailsAnswerDtoTest {

  @Test
  fun `should map empty delius values to empty list values in dto`() {
    // Given
    val personalContact = createEmptyPersonalContact()

    // When
    val gpDetailsAnswerDto = GPDetailsAnswerDto.from(personalContact = personalContact)

    // Then
    assertThat(gpDetailsAnswerDto.name).isEmpty()
    assertThat(gpDetailsAnswerDto.buildingName).isEmpty()
    assertThat(gpDetailsAnswerDto.addressNumber).isEmpty()
    assertThat(gpDetailsAnswerDto.streetName).isEmpty()
    assertThat(gpDetailsAnswerDto.district).isEmpty()
    assertThat(gpDetailsAnswerDto.town).isEmpty()
    assertThat(gpDetailsAnswerDto.county).isEmpty()
    assertThat(gpDetailsAnswerDto.postcode).isEmpty()
    assertThat(gpDetailsAnswerDto.telephoneNumber).isEmpty()
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
