package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEntity
import java.time.LocalDateTime
import java.util.UUID

class AssessmentDto(

  @Schema(description = "Assessment primary key", example = "1234")
  val assessmentId: Long? = null,

  @Schema(description = "Assessment UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val assessmentUuid: UUID? = null,

  @Schema(description = "Supervision ID", example = "1234")
  val supervisionId: String? = null,

  @Schema(description = "Created Date", example = "2020-01-02T16:00:00")
  val createdDate: LocalDateTime? = null,

  @Schema(description = "Completed Date", example = "2020-01-02T16:00:00")
  val completedDate: LocalDateTime? = null

) {

  companion object {

    fun from(assessment: AssessmentEntity?): AssessmentDto {
      return AssessmentDto(
        assessment?.assessmentId,
        assessment?.assessmentUuid,
        assessment?.supervisionId,
        assessment?.createdDate,
        assessment?.completedDate
      )
    }
  }
}
