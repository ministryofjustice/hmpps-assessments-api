package uk.gov.justice.digital.assessments.restclient.communityapi

data class CommunityOffenderPersonalContactsDto(
  val contacts: List<Contact>? = null
)

data class Contact(
  val firstName: String? = null,
  val relationship: String? = null,
  val mobileNumber: String? = null,
  val relationshipType: Type? = null,
  val address: Address? = null
)
