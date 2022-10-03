package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusPersonalCircumstanceDto
import uk.gov.justice.digital.assessments.restclient.communityapi.PersonalCircumstanceType

class CarerCommitmentsAnswerDtoTest {
  @Test
  fun `should map empty delius values to empty list values in dto`() {

    // Given
    val deliusPersonalCircumstanceDto = DeliusPersonalCircumstanceDto(
      personalCircumstanceType = PersonalCircumstanceType(
        code = "code",
        description = "description"
      ),
      personalCircumstanceSubType = PersonalCircumstanceType(
        code = "code",
        description = "description"
      ),
      notes = null,
      evidenced = false,
      isActive = false
    )

    // When
    val carerCommitmentsAnswerDto = CarerCommitmentsAnswerDto.from(deliusPersonalCircumstanceDto = deliusPersonalCircumstanceDto)

    // Then
    assertThat(carerCommitmentsAnswerDto.notes).isNull()
  }
}
