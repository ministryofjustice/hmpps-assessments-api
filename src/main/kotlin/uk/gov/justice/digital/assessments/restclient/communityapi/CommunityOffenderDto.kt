package uk.gov.justice.digital.assessments.restclient.communityapi

import uk.gov.justice.digital.assessments.api.DisabilityAnswerDto
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity

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
) {
  companion object {
    fun from(communityOffenderDto: CommunityOffenderDto, episode: AssessmentEpisodeEntity) {
      val offenderProfile = communityOffenderDto.offenderProfile
      val contactDetails = communityOffenderDto.contactDetails

      episode.addAnswer("first_name", listOf(communityOffenderDto.firstName).orEmpty() as List<Any>)
      episode.addAnswer("first_name_aliases", communityOffenderDto.offenderAliases?.mapNotNull { it.firstName }.orEmpty())
      episode.addAnswer("family_name", listOf(communityOffenderDto.surname).orEmpty() as List<Any>)
      episode.addAnswer("family_name_aliases", communityOffenderDto.offenderAliases?.mapNotNull { it.surname }.orEmpty())
      episode.addAnswer("dob", listOf(communityOffenderDto.dateOfBirth))
      episode.addAnswer("dob_aliases", communityOffenderDto.offenderAliases?.mapNotNull { it.dateOfBirth }.orEmpty())
      val addresses = communityOffenderDto.contactDetails?.addresses?.filter { it.status?.code == "M" }
      episode.addAnswer("contact_address_building_name", addresses?.mapNotNull { it.buildingName }.orEmpty() as List<Any>)
      episode.addAnswer("contact_address_house_number", addresses?.mapNotNull { it.addressNumber }.orEmpty() as List<Any>)
      episode.addAnswer("contact_address_street_name", addresses?.mapNotNull { it.streetName }.orEmpty() as List<Any>)
      episode.addAnswer("contact_address_district", addresses?.mapNotNull { it.district }.orEmpty() as List<Any>)
      episode.addAnswer("contact_address_town_or_city", addresses?.mapNotNull { it.town }.orEmpty() as List<Any>)
      episode.addAnswer("contact_address_county", addresses?.mapNotNull { it.county }.orEmpty() as List<Any>)
      episode.addAnswer("contact_address_postcode", addresses?.mapNotNull { it.postcode }.orEmpty() as List<Any>)
      episode.addAnswer("crn", listOf(communityOffenderDto.otherIds?.crn).orEmpty() as List<Any>)
      episode.addAnswer("pnc", listOf(communityOffenderDto.otherIds?.pncNumber).orEmpty() as List<Any>)
      episode.addAnswer("ethnicity", listOf(communityOffenderDto.offenderProfile?.ethnicity).orEmpty() as List<Any>)
      episode.addAnswer("gender", listOf(communityOffenderDto?.gender?.uppercase()).orEmpty() as List<Any>)
      episode.addAnswer("gender_identity", listOf(mapGenderIdentity(offenderProfile?.genderIdentity)).orEmpty() as List<Any>)
      episode.addAnswer("language", listOf(offenderProfile?.offenderLanguages?.primaryLanguage).orEmpty() as List<Any>)
      episode.addAnswer("requires_interpreter", listOf(offenderProfile?.offenderLanguages?.requiresInterpreter.toString()).orEmpty() as List<Any>)
      episode.addAnswer("contact_email_addresses", contactDetails?.emailAddresses.orEmpty() as List<Any>)
      episode.addAnswer(
        "contact_mobile_phone_number",
        contactDetails?.phoneNumbers?.filter { it.type == "MOBILE" }?.map { it.number }.orEmpty() as List<Any>
      )
      episode.addAnswer(
        "contact_phone_number",
        contactDetails?.phoneNumbers?.filter { it.type == "TELEPHONE" }?.map { it.number }.orEmpty() as List<Any>
      )

      val physicalDisabilityCodeTypes = listOf("D", "D02", "RM", "RC", "PC", "VI", "HD")
      episode.addAnswer(
        "physical_disability",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code in physicalDisabilityCodeTypes
        }?.map { it.disabilityType.code }.orEmpty()
      )
      episode.addAnswer(
        "physical_disability_details",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code in physicalDisabilityCodeTypes
        }?.map { it.disabilityType.description }.orEmpty()
      )
      episode.addAnswer(
        "learning_disability",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code == "LA"
        }?.map { it.disabilityType.code }.orEmpty()
      )
      episode.addAnswer(
        "learning_disability_details",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code == "LA"
        }?.map { it.disabilityType.description }.orEmpty()
      )
      episode.addAnswer(
        "learning_difficulty",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code == "LD"
        }?.map { it.disabilityType.code }
      )
      episode.addAnswer(
        "learning_difficulty_details",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code == "LD"
        }?.map { it.disabilityType.description }.orEmpty()
      )
      val mentalHealthConditionCodeTypes = listOf("D", "D01", "M1")
      episode.addAnswer(
        "mental_health_condition",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code in mentalHealthConditionCodeTypes
        }?.map { it.disabilityType.code }.orEmpty()
      )
      episode.addAnswer(
        "mental_health_condition_details",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code in mentalHealthConditionCodeTypes
        }?.map { it.disabilityType.description }.orEmpty()
      )
      val activeDisabilityCodeTypes =
        listOf("AP", "DY", "VI", "SI", "HD", "LD", "RD", "MI", "PC", "RM", "RC", "SD", "RF", "OD", "AS", "LA")
      episode.addAnswer(
        "active_disabilities",
        offenderProfile?.disabilities?.filter {
          it.disabilityType.code in activeDisabilityCodeTypes && it.isActive
        }?.map { DisabilityAnswerDto.from(it) }.orEmpty()
      )
    }

    private fun mapGenderIdentity(genderIdentity: String?): String? {
      return genderIdentity?.uppercase()
        ?.replace(' ', '_')
        ?.replace('-', '_')
    }
  }
}

data class OffenderProfile(
  val ethnicity: String? = null,
  val disabilities: List<DeliusDisabilityDto>? = null,
  val offenderLanguages: OffenderLanguages? = null,
  val genderIdentity: String? = null,
)

data class OffenderLanguages(val primaryLanguage: String? = null, val requiresInterpreter: Boolean)

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
