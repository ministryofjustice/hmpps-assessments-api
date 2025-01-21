package uk.gov.justice.digital.assessments.api.answers

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact

data class EmergencyContactDetailsAnswerDto(

  @JsonProperty("emergency_contact_first_name")
  val firstName: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_family_name")
  val familyName: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_relationship")
  var relationship: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_house_number")
  var addressNumber: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_building_name")
  val buildingName: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_street_name")
  val streetName: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_district")
  val district: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_town_or_city")
  val town: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_county")
  val county: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_address_postcode")
  val postcode: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_phone_number")
  val telephoneNumber: List<String?> = emptyList(),

  @JsonProperty("emergency_contact_mobile_phone_number")
  val mobileNumber: List<String?> = emptyList(),
) {
  companion object {

    fun from(personalContacts: List<PersonalContact>?): List<EmergencyContactDetailsAnswerDto> = if (personalContacts.isNullOrEmpty()) {
      emptyList()
    } else {
      personalContacts.map { from(it) }
    }

    fun from(personalContact: PersonalContact): EmergencyContactDetailsAnswerDto = EmergencyContactDetailsAnswerDto(

      firstName = listOfNotNull(personalContact.name.forename),
      familyName = listOfNotNull(personalContact.name.surname),
      relationship = listOfNotNull(personalContact.relationship),
      addressNumber = listOfNotNull(personalContact.address?.addressNumber),
      buildingName = listOfNotNull(personalContact.address?.buildingName),
      streetName = listOfNotNull(personalContact.address?.streetName),
      district = listOfNotNull(personalContact.address?.district),
      town = listOfNotNull(personalContact.address?.town),
      county = listOfNotNull(personalContact.address?.county),
      postcode = listOfNotNull(personalContact.address?.postcode),
      telephoneNumber = listOfNotNull(personalContact.telephoneNumber),
      mobileNumber = listOfNotNull(personalContact.mobileNumber),
    )
  }
}
