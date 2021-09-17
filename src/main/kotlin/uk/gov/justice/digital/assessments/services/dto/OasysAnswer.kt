package uk.gov.justice.digital.assessments.services.dto

data class OasysAnswer(
  val sectionCode: String,
  val logicalPage: Long? = null,
  val questionCode: String,
  val answer: String,
  val isStatic: Boolean? = false
)
