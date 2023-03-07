package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.answers.GPDetailsAnswerDto
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact

class GPDetailsAnswerDtoTest {

  @Test
  fun `should map empty delius values to empty list values in dto`() {
    // Given
    val personalContact = createEmptyPersonalContact()

    // When
    val gpDetailsAnswerDto = GPDetailsAnswerDto.from(personalContact = personalContact)

    // Then
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
