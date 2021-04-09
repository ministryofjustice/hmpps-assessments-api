package uk.gov.justice.digital.assessments.restclient.assessmentapi

data class OASysQuestionDto(
  var refQuestionId: Long? = null,
  val refQuestionCode: String? = null,
  val oasysQuestionId: Long? = null,
  val displayOrder: Long? = null,
  val displayScore: Long? = null,
  val questionText: String? = null,
  val currentlyHidden: Boolean? = null,
  val disclosed: Boolean? = null,
  val answers: Collection<OASysAnswerDto> = emptySet()
)
