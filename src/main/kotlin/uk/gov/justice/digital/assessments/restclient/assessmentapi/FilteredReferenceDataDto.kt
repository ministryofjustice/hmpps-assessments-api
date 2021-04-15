package uk.gov.justice.digital.assessments.restclient.assessmentapi

data class FilteredReferenceDataDto(
  val oasysSetPk: Long,
  val oasysUserCode: String,
  val oasysAreaCode: String,
  val team: String?,
  val offenderPk: Long?,
  val assessor: String?,
  val assessmentType: String,
  val assessmentStage: String,
  val sectionCode: String,
  val fieldName: String,
  val parentList: Map<String, String>?
)
