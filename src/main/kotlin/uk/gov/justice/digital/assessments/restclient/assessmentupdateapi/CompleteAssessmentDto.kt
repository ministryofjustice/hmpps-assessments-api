package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

class CompleteAssessmentDto(
  val oasysSetPk: Long,
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val ignoreWarnings: Boolean
)
