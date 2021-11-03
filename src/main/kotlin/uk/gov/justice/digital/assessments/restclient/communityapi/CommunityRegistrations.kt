package uk.gov.justice.digital.assessments.restclient.communityapi

data class CommunityRegistrations(
  val registrations: Collection<CommunityRegistration> = emptyList()
)

data class CommunityRegistration(
  val active: Boolean,
  val warnUser: Boolean,
  val riskColour: String,
  val registerCategory: CommunityRegistrationElement? = null,
  val registerLevel: CommunityRegistrationElement? = null,
  val type: CommunityRegistrationElement,
)

class CommunityRegistrationElement(
  val code: String,
  val description: String,
)
