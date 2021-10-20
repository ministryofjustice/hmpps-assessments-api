package uk.gov.justice.digital.assessments.restclient.communityapi

data class CommunityOffenderDto(
  var offenderId: Long? = null,
  val firstName: String? = null,
  val middleNames: List<String>? = null,
  val surname: String? = null,
  val previousSurname: String? = null,
  val dateOfBirth: String,
  val gender: String? = null,
  val otherIds: IDs? = null,
  val offenderAliases: List<OffenderAlias>? = null,
  val contactDetails: ContactDetails? = null,
  val offenderProfile: OffenderProfile? = null
)

class OffenderProfile(
  val ethnicity: String? = null,
  val disabilities: List<Disability>? = null
)

class Disability(
  val disabilityType: DisabilityType,
)

class DisabilityType(
  val code: String? = null,
  val description: String? = null
)

data class ContactDetails(
  val emailAddresses: List<String>? = null,
  val phoneNumbers: List<Phone>? = null
)

class Phone(
  val number: String? = null,
  val type: String? = null
)

data class OffenderAlias(
  val firstName: String? = null,
  val surname: String? = null,
  val dateOfBirth: String? = null,
)

data class IDs(
  val crn: String? = null,
  val pncNumber: String? = null,
  val croNumber: String? = null,
  val niNumber: String? = null,
  val nomsNumber: String? = null,
  val immigrationNumber: String? = null
)
