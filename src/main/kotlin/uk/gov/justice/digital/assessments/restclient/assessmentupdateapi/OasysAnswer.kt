package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

data class OasysAnswer(
  val sectionCode: String,
  val logicalPage: Long? = null,
  val questionCode: String,
  val answer: String? = null,
  val isStatic: Boolean? = false
)
