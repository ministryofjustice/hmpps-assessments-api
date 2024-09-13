package uk.gov.justice.digital.assessments.restclient.deliusintegrationapi

data class UserAccessResponse(
  val exclusionMessage: String?,
  val restrictionMessage: String?,
  val userExcluded: Boolean,
  val userRestricted: Boolean,
)
