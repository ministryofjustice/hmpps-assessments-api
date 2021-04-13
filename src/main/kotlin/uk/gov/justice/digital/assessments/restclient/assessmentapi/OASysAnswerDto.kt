package uk.gov.justice.digital.assessments.restclient.assessmentapi

data class OASysAnswerDto(
  val refAnswerCode: String? = null,
  val oasysAnswerId: Long? = null,
  val refAnswerId: Long? = null,
  val displayOrder: Long? = null,
  val staticText: String? = null,
  val freeFormText: String? = null,
  val ogpScore: Int? = null,
  val ovpScore: Int? = null,
  val qaRawScore: Int? = null
)
