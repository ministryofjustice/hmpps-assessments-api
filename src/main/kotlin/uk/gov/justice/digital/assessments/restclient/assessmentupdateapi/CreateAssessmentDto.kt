package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

import uk.gov.justice.digital.assessments.jpa.entities.OasysAssessmentType

class CreateAssessmentDto(
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val assessmentType: OasysAssessmentType,
  val teamCode: String? = null,
  val assessorCode: String? = null
)
