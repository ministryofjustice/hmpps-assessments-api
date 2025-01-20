package uk.gov.justice.digital.assessments.api.answers

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalContact

data class GPDetailsAnswerDto(
  @JsonProperty("gp_name")
  val name: List<String?> = emptyList(),

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
) {
  companion object {
    fun from(personalContacts: List<PersonalContact>?): List<GPDetailsAnswerDto> = if (personalContacts.isNullOrEmpty()) {
      emptyList()
    } else {
      personalContacts.map { from(it) }
    }

    fun from(personalContact: PersonalContact): GPDetailsAnswerDto {
      val fullName = listOfNotNull(personalContact.name.forename, personalContact.name.surname)
        .joinToString(separator = " ")

      return GPDetailsAnswerDto(
        name = if (fullName.isEmpty()) emptyList() else listOf(fullName),
        buildingName = listOfNotNull(personalContact.address?.buildingName),
        addressNumber = listOfNotNull(personalContact.address?.addressNumber),
        streetName = listOfNotNull(personalContact.address?.streetName),
        district = listOfNotNull(personalContact.address?.district),
        town = listOfNotNull(personalContact.address?.town),
        county = listOfNotNull(personalContact.address?.county),
        postcode = listOfNotNull(personalContact.address?.postcode),
        telephoneNumber = listOfNotNull(personalContact.telephoneNumber, personalContact.mobileNumber),
      )
    }
  }
}
