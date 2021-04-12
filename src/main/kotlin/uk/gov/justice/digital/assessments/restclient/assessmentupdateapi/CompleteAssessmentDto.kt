package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

class CompleteAssessmentDto(
  val oasysSetPk: Long,
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val assessmentType: AssessmentType,
  val ignoreWarnings: Boolean
)
