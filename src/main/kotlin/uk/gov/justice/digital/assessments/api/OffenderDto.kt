package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import java.time.LocalDate

data class OffenderDto(
  val firstName: String? = null,
  val surname: String? = null,
  val dateOfBirth: LocalDate,
  val gender: String? = null,
  val crn: String? = null,
  val pncNumber: String? = null,
  val croNumber: String? = null,
  var offence: OffenceDto? = null,
  val firstNameAliases: List<String>? = emptyList(),
  val surnameAliases: List<String>? = emptyList(),
  val dateOfBirthAliases: List<String>? = emptyList(),
) {
  companion object {

    fun from(caseDetails: CaseDetails, eventId: Long?): OffenderDto {
      return OffenderDto(
        firstName = caseDetails.name.forename,
        surname = caseDetails.name.surname,
        dateOfBirth = caseDetails.dateOfBirth,
        gender = caseDetails.gender,
        crn = caseDetails.crn,
        pncNumber = caseDetails.pncNumber,
        croNumber = caseDetails.croNumber,
        offence = OffenceDto.from(caseDetails.sentence, eventId),
        firstNameAliases = caseDetails.aliases?.map { it.name.forename },
        surnameAliases = caseDetails.aliases?.map { it.name.surname },
        dateOfBirthAliases = caseDetails.aliases?.map { it.dateOfBirth.toString() },
      )
    }
  }
}
