package uk.gov.justice.digital.assessments.jpa.entities.assessments

import java.time.LocalDateTime

class GPDetailsAnswerEntity (
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
  val createdDatetime: LocalDateTime?,
  val lastUpdatedDatetime: LocalDateTime?,
  val practiceName: String?,
  var addressNumber: String?,
  val buildingName: String?,
  val streetName: String?,
  val district: String?,
  val town: String?,
  val county: String?,
  val postcode: String?,
  val telephoneNumber: String?,
  val isActive: Boolean?,
)