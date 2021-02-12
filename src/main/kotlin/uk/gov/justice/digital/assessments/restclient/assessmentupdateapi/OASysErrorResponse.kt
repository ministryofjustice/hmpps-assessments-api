package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

data class OASysErrorResponse(
  val status: Int,
  val errorCode: Int? = null,
  val userMessage: String? = null,
  val developerMessage: String? = null,
  val moreInfo: String? = null,
  val previousOasysSetPk: Long? = null,
  val offenderPk: Long? = null
)
