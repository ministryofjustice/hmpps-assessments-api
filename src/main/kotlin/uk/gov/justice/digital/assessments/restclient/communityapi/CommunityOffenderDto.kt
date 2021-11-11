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

data class OffenderProfile(
  val ethnicity: String? = null,
  val disabilities: List<Disability>? = null,
  val offenderLanguages: OffenderLanguages? = null,
  val genderIdentity: String? = null,
)

data class OffenderLanguages(val primaryLanguage: String? = null, val requiresInterpreter: Boolean)

data class Disability(
  val disabilityType: DisabilityType,
)

data class DisabilityType(
  val code: String? = null,
  val description: String? = null
)

data class ContactDetails(
  val emailAddresses: List<String>? = null,
  val phoneNumbers: List<Phone>? = null,
  val addresses: List<Address>? = null
)

data class Address(
  val addressNumber: String? = null,
  val buildingName: String? = null,
  val status: Type? = null,
  val county: String? = null,
  val district: String? = null,
  val postcode: String? = null,
  val streetName: String? = null,
  val town: String? = null,
  val telephoneNumber: String? = null,
)

data class Type(
  val code: String? = null,
  val description: String? = null,
)

data class Phone(
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
