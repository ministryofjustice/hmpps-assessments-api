package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.SubjectEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.UUID

class AssessmentSubjectDto(
  @Schema(description = "Assessment UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val assessmentUuid: UUID? = null,

  @Schema(description = "Subject name", example = "John Smith")
  val name: String? = null,

  @Schema(description = "PNC number, if known")
  val pnc: String? = null,

  @Schema(description = "CRN, if known")
  val crn: String? = null,

  @Schema(description = "Date of birth", example = "1929-08-08")
  val dob: LocalDate? = null,

  @Schema(description = "Subject Record created Date", example = "2020-01-02T16:00:00")
  val createdDate: LocalDateTime? = null,
) {
  val age: Int? get() = dob?.let { Period.between(dob, LocalDate.now()).years }

  companion object {
    fun from(subject: SubjectEntity?): AssessmentSubjectDto? {
      if (subject == null) return null
      return AssessmentSubjectDto(
        subject.assessment?.assessmentUuid,
        subject.name,
        subject.pnc,
        subject.crn,
        subject.dateOfBirth,
        subject.createdDate
      )
    }
  }
}
