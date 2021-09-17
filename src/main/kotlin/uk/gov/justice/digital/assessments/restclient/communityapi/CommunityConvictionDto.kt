package uk.gov.justice.digital.assessments.restclient.communityapi

import java.time.LocalDate
import javax.persistence.EntityNotFoundException

data class CommunityConvictionDto(
  val convictionId: Long? = null,
  val offences: List<CommunityOffenceDto>? = null,
  val convictionDate: LocalDate? = null,
  val index: Long,
  val sentence: Sentence? = null
)

class CommunityOffenceDto(
  val offenceId: String? = null,
  val mainOffence: Boolean? = null,
  val detail: CommunityOffenceDetail? = null
) {
  companion object {
    fun getMainOffence(offences: List<CommunityOffenceDto>?): CommunityOffenceDto {
      if (offences.isNullOrEmpty()) {
        throw EntityNotFoundException("No offences found")
      } else {
        return offences.first { it.mainOffence == true }
      }
    }
  }
}

class CommunityOffenceDetail(
  val mainCategoryCode: String,
  val mainCategoryDescription: String,
  val subCategoryCode: String,
  val subCategoryDescription: String
)

class Sentence(
  val startDate: LocalDate
)
