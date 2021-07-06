package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType

class CompleteAssessmentDto(
  val oasysSetPk: Long,
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val oasysAssessmentType: OasysAssessmentType,
  val ignoreWarnings: Boolean
)
