package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.SubjectEntity
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentDto(

  @Schema(description = "Assessment UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val assessmentUuid: UUID? = null,

  @Schema(description = "Created Date", example = "2020-01-02T16:00:00")
  val createdDate: LocalDateTime? = null,

  @Schema(description = "Completed Date", example = "2020-01-02T16:00:00")
  val completedDate: LocalDateTime? = null,

  @Schema(description = "subject")
  val subject: SubjectDto? = null

) {

  companion object {

    fun from(assessment: AssessmentEntity?): AssessmentDto {
      return AssessmentDto(
        assessment?.assessmentUuid,
        assessment?.createdDate,
        assessment?.completedDate,
        assessment?.subject.toSubjectDto()
      )
    }

    private fun SubjectEntity?.toSubjectDto(): SubjectDto? {
      return this?.let {
        SubjectDto(
          it.subjectUuid,
          it.name,
          it.pnc,
          it.crn,
          it.dateOfBirth,
          it.gender
        )
      }
    }
  }
}
