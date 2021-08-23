package uk.gov.justice.digital.assessments.utils.offenderStubResource

data class OffenderStubDto(
  val crn: String? = null,
  val pnc: String?,
  val familyName: String?,
  val forename1: String?,
  val areaCode: String,
)
