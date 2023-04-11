package uk.gov.justice.digital.assessments.restclient

data class ApiErrorResponse(
  val status: String,
  val developerMessage: String,
)
