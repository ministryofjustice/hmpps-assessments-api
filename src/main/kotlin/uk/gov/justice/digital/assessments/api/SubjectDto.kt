package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.util.UUID

class SubjectDto(
  @Schema(description = "Subject UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val subjectUuid: UUID = UUID.randomUUID(),

  @Schema(description = "Source of subject data", example = "DELIUS")
  val source: String,

  @Schema(description = "Source of subject data id", example = "123")
  val sourceId: String,

  @Schema(description = "Subject name", example = "Gary Hart")
  val name: String? = null,

  @Schema(description = "Subject pnc", example = "19281028")
  val pnc: String? = null,

  @Schema(description = "Subject crn", example = "18881028")
  val crn: String,

  @Schema(description = "Subject date of birth", example = "01/02/2001")
  val dateOfBirth: LocalDate,

  @Schema(description = "Subject gender", example = "MALE")
  val gender: String? = null,
)