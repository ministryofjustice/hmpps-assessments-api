package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import java.time.LocalDate

data class OffenderDto(
  var offenderId: Long? = null,
  val firstName: String? = null,
  val surname: String? = null,
  val dateOfBirth: LocalDate,
  val gender: String? = null,
  val crn: String? = null,
  val pncNumber: String? = null,
  val croNumber: String? = null,
  var offence: OffenceDto? = null,
  val address: Address? = null,
  val firstNameAliases: List<String>? = emptyList(),
  val surnameAliases: List<String>? = emptyList(),
  val dateOfBirthAliases: List<String>? = emptyList()
) {
  companion object {

    fun from(communityOffenderDto: CommunityOffenderDto): OffenderDto {
      return OffenderDto(
        offenderId = communityOffenderDto.offenderId,
        firstName = communityOffenderDto.firstName,
        surname = communityOffenderDto.surname,
        dateOfBirth = LocalDate.parse(communityOffenderDto.dateOfBirth),
        gender = communityOffenderDto.gender,
        crn = communityOffenderDto.otherIds?.crn,
        pncNumber = communityOffenderDto.otherIds?.pncNumber,
        croNumber = communityOffenderDto.otherIds?.croNumber,
        firstNameAliases = communityOffenderDto.offenderAliases?.mapNotNull { it.firstName },
        surnameAliases = communityOffenderDto.offenderAliases?.mapNotNull { it.surname },
        dateOfBirthAliases = communityOffenderDto.offenderAliases?.mapNotNull { it.dateOfBirth }
      )
    }
  }
}

data class Address(
  val address1: String? = null,
  val address2: String? = null,
  val address3: String? = null,
  val address4: String? = null,
  val address5: String? = null,
  val postcode: String? = null
)
