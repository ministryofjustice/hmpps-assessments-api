package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import java.time.LocalDateTime
import java.util.UUID

class AssessmentDtoTest {

  @Test
  fun `builds valid Assessment DTO`() {

    val assessmentEntity = AssessmentEntity(
      1,
      UUID.randomUUID(),
      LocalDateTime.of(2019, 8, 1, 8, 0),
      LocalDateTime.of(2020, 8, 1, 8, 0)
    )

    val assessmentDto = AssessmentDto.from(assessmentEntity)

    assertThat(assessmentDto.createdDate).isEqualTo(assessmentEntity.createdDate)
    assertThat(assessmentDto.completedDate).isEqualTo(assessmentEntity.completedDate)
  }
}
