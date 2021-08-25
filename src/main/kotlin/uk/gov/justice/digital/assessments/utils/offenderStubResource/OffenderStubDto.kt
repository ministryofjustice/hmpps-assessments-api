package uk.gov.justice.digital.assessments.utils.offenderStubResource

import java.time.LocalDate

data class OffenderStubDto(
  val crn: String? = null,
  val pnc: String?,
  val familyName: String?,
  val forename1: String?,
  val dateOfBirth: LocalDate?,
  val gender: String?,
  val areaCode: String,
)
