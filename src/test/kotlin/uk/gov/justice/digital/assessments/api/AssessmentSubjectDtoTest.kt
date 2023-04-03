package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class AssessmentSubjectDtoTest {

  @Test
  fun `builds valid Assessment Subject DTO with calculated age`() {

    val clock = Clock.fixed(Instant.parse("2022-03-06T12:18:05Z"), ZoneId.of("Europe/London"))

    val subjectEntity = SubjectEntity(
      1,
      UUID.randomUUID(),
      "name",
      "pnc",
      "crn",
      LocalDate.of(2001, 8, 1),
      "Male",
      LocalDateTime.of(2020, 8, 1, 8, 0)
    )

    val assessmentSubjectDto = AssessmentSubjectDto.from(subjectEntity, clock)

    assertThat(assessmentSubjectDto?.dob).isEqualTo(subjectEntity.dateOfBirth)
    assertThat(assessmentSubjectDto?.age).isEqualTo(20)
  }
}
