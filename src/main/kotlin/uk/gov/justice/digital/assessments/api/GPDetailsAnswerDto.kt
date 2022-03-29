package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.communityapi.PersonalContact

class GPDetailsAnswerDto(

  @JsonProperty("gp_first_name")
  val firstName: List<String?> = emptyList(),

  @JsonProperty("gp_family_name")
  val familyName: List<String?> = emptyList(),

  @JsonProperty("gp_practice_name")
  val practiceName: List<String?> = emptyList(),

  @JsonProperty("gp_address_house_number")
  var addressNumber: List<String?> = emptyList(),

  @JsonProperty("gp_address_building_name")
  val buildingName: List<String?> = emptyList(),

  @JsonProperty("gp_address_street_name")
  val streetName: List<String?> = emptyList(),

  @JsonProperty("gp_address_district")
  val district: List<String?> = emptyList(),

  @JsonProperty("gp_address_town_or_city")
  val town: List<String?> = emptyList(),

  @JsonProperty("gp_address_county")
  val county: List<String?> = emptyList(),

  @JsonProperty("gp_address_postcode")
  val postcode: List<String?> = emptyList(),

  @JsonProperty("gp_phone_number")
  val telephoneNumber: List<String?> = emptyList(),
){
  companion object {

    fun from(personalContacts: List<PersonalContact>): List<GPDetailsAnswerDto>{
      return personalContacts.map { from(it) }
    }

    fun from(personalContact: PersonalContact): GPDetailsAnswerDto {
      return GPDetailsAnswerDto(

        firstName = listOf(personalContact.firstName),
        familyName = listOf(personalContact.surname),
        addressNumber = listOf(personalContact.address?.addressNumber),
        buildingName = listOf(personalContact.address?.buildingName),
        streetName = listOf(personalContact.address?.streetName),
        district = listOf(personalContact.address?.district),
        town = listOf(personalContact.address?.town),
        county = listOf(personalContact.address?.county),
        postcode = listOf(personalContact.address?.postcode),
        telephoneNumber = listOf(personalContact.address?.telephoneNumber)
      )
    }
  }
}