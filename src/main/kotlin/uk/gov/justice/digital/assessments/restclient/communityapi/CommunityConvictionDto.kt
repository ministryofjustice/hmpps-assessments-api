package uk.gov.justice.digital.assessments.restclient.communityapi

import java.time.LocalDate

data class CommunityConvictionDto(
  val convictionId: Long? = null,
  val offences: List<Offence>? = null,
  val convictionDate: LocalDate? = null,
  val index: Long
)

class Offence(
  val offenceId: String? = null,
  val mainOffence: Boolean? = null,
  val detail: OffenceDetail? = null
)

class OffenceDetail(
  val code: String? = null,
  val description: String? = null,
  val mainCategoryCode: String? = null,
  val mainCategoryDescription: String? = null,
  val subCategoryCode: String? = null,
  val subCategoryDescription: String? = null
)
