package uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

class CreateAssessmentDto(
  val offenderPk: Long,
  val areaCode: String,
  val oasysUserCode: String,
  val assessmentType: AssessmentType,
  val teamCode: String? = null,
  val assessorCode: String? = null
)
