package uk.gov.justice.digital.assessments.restclient.communityapi

// import uk.gov.justice.digital.assessments.api.answers.EmergencyContactDetailsAnswerDto
// import uk.gov.justice.digital.assessments.api.answers.GPDetailsAnswerDto
// import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
// import java.time.LocalDateTime
//
// data class PersonalContact(
//   val personalContactId: Long?,
//   val relationship: String?,
//   val startDate: LocalDateTime?,
//   val endDate: LocalDateTime?,
//   val title: String?,
//   val firstName: String?,
//   val otherNames: String?,
//   val surname: String?,
//   val previousSurname: String?,
//   val mobileNumber: String?,
//   val emailAddress: String?,
//   val notes: String?,
//   val gender: String?,
//   val relationshipType: RelationshipType?,
//   val createdDatetime: LocalDateTime?,
//   val lastUpdatedDatetime: LocalDateTime?,
//   val address: AddressSummary?,
//   val isActive: Boolean?,
// ) {
//   companion object {
//     fun from(personalContacts: List<PersonalContact>, episode: AssessmentEpisodeEntity) {
//       mapEmergencyContacts(personalContacts, episode)
//       mapGPDetails(personalContacts, episode)
//     }
//
//     private fun mapGPDetails(
//       personalContacts: List<PersonalContact>,
//       episode: AssessmentEpisodeEntity
//     ) {
//       val gpDetails = personalContacts.filter { it.relationshipType?.code == "RT02" && it.isActive == true }
//       episode.addAnswer("gp_details", GPDetailsAnswerDto.from(gpDetails) as List<Any>)
//     }
//
//     private fun mapEmergencyContacts(
//       personalContacts: List<PersonalContact>,
//       episode: AssessmentEpisodeEntity
//     ) {
//       val emergencyContacts = personalContacts.filter { it.relationshipType?.code == "ME" && it.isActive == true }
//       episode.addAnswer("emergency_contact_details", EmergencyContactDetailsAnswerDto.from(emergencyContacts) as List<Any>)
//     }
//   }
// }

class RelationshipType(
  val code: String?,
  val description: String?,
)

class AddressSummary(
  val addressNumber: String?,
  val buildingName: String?,
  val streetName: String?,
  val district: String?,
  val town: String?,
  val county: String?,
  val postcode: String?,
  val telephoneNumber: String?,
)
