package uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType

class CreateAssessmentDto(
  val offenderPk: Long,
  val area: String,
  val user: String,
  val assessmentType: AssessmentType,
  val team: String? = null,
  val assessor: String? = null
)
