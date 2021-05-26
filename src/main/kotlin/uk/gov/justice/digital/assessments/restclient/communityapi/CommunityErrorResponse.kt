package uk.gov.justice.digital.assessments.restclient.communityapi

data class CommunityErrorResponse(
  val status: Int,
  val userMessage: String? = null,
  val developerMessage: String? = null
)
