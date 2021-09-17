package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

import uk.gov.justice.digital.assessments.jpa.entities.refdata.OasysAssessmentType
import uk.gov.justice.digital.assessments.services.dto.OasysAnswer

data class UpdateAssessmentAnswersDto(
  val oasysSetPk: Long,
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val answers: Set<OasysAnswer>? = emptySet(),
  val assessmentType: OasysAssessmentType,
)
