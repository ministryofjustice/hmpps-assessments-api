package uk.gov.justice.digital.assessments.restclient.communityapi


import java.time.LocalDateTime

class PersonalContact (
  val personalContactId: Long?,
  val relationship: String?,
  val startDate: LocalDateTime?,
  val endDate: LocalDateTime?,
  val title: String?,
  val firstName: String?,
  val otherNames: String?,
  val surname: String?,
  val previousSurname: String?,
  val mobileNumber: String?,
  val emailAddress: String?,
  val notes: String?,
  val gender: String?,
//  val relationshipType: KeyValue?,
  val createdDatetime: LocalDateTime?,
  val lastUpdatedDatetime: LocalDateTime?,
//  val address: AddressSummary?,
  val isActive: Boolean?,
)

class KeyValue (
  val code: String?,
  val description: String?,
)

class AddressSummary (
  val addressNumber: String?,
  val buildingName: String?,
  val streetName: String?,
  val district: String?,
  val town: String?,
  val county: String?,
  val postcode: String?,
  val telephoneNumber: String?,
)
