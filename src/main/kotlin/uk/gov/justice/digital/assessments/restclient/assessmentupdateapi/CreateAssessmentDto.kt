package uk.gov.justice.digital.hmpps.offenderassessmentsupdates.api

class CreateAssessmentDto(
  val offenderPk: Long,
  val area: String,
  val user: String,
  val assessmentType: String,
  val team: String? = null,
  val assessor: String? = null
)
