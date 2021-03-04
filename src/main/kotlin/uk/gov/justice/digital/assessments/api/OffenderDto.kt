package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.DefendantAddress
import java.time.LocalDate

data class OffenderDto(
  var offenderId: Long? = null,
  val firstName: String? = null,
  val surname: String? = null,
  val dateOfBirth: LocalDate? = null,
  val gender: String? = null,
  val crn: String? = null,
  val pncNumber: String? = null,
  val croNumber: String? = null,
  val offence: OffenceDto? = null,
  val address: Address? = null

) {
  companion object {

    fun from(communityOffenderDto: CommunityOffenderDto): OffenderDto {
      return OffenderDto(
        offenderId = communityOffenderDto.offenderId,
        firstName = communityOffenderDto.firstName,
        surname = communityOffenderDto.surname,
        dateOfBirth = communityOffenderDto.dateOfBirth,
        gender = communityOffenderDto.gender,
        crn = communityOffenderDto.otherIds?.crn,
        pncNumber = communityOffenderDto.otherIds?.pncNumber,
        croNumber = communityOffenderDto.otherIds?.croNumber
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
  val address6: String? = null,
  val postcode: String? = null
) {
  companion object {

    fun from(defendantAddress: DefendantAddress?): Address? {
      return if (defendantAddress == null) {
        null
      } else {
        Address(
          address1 = defendantAddress.line1,
          address2 = defendantAddress.line2,
          address3 = defendantAddress.line3,
          address4 = defendantAddress.line4,
          address5 = defendantAddress.line5,
          postcode = defendantAddress.postcode
        )
      }
    }
  }
}
