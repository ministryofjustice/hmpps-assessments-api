package uk.gov.justice.digital.assessments.restclient.communityapi

import java.time.LocalDate

data class CommunityOffenderDto(
  var offenderId: Long? = null,
  val firstName: String? = null,
  val middleNames: List<String>? = null,
  val surname: String? = null,
  val previousSurname: String? = null,
  val dateOfBirth: LocalDate? = null,
  val gender: String? = null,
  val otherIds: IDs? = null
)

data class IDs(
  val crn: String? = null,
  val pncNumber: String? = null,
  val croNumber: String? = null,
  val niNumber: String? = null,
  val nomsNumber: String? = null,
  val immigrationNumber: String? = null
)
