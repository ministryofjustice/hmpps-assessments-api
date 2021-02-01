package uk.gov.justice.digital.assessments.restclient.communityapi

import java.time.LocalDate

data class GetOffencesDto(
  private var offenderId: Long? = null,
  private val firstName: String? = null,
  private val middleNames: List<String>? = null,
  private val surname: String? = null,
  private val previousSurname: String? = null,
  private val dateOfBirth: LocalDate? = null,
  private val gender: String? = null,
  private val otherIds: IDs? = null
)
