package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

class CreateAssessmentDto(
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val teamCode: String? = null,
  val assessorCode: String? = null
)
