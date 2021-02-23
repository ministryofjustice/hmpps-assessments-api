package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
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
  val offence: OffenceDto? = null

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
