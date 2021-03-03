package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

data class UpdateAssessmentAnswersDto(
  val oasysSetPk: Long,
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val answers: Set<OasysAnswer>? = emptySet(),
  val assessmentType: AssessmentType,
)
