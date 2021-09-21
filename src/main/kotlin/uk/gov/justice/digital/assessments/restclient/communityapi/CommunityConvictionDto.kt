package uk.gov.justice.digital.assessments.restclient.communityapi

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

data class CommunityConvictionDto @JsonCreator constructor(
  val convictionId: Long? = null,
  val sentence: Sentence? = null,
  val offences: List<CommunityOffenceDto>? = null,
  val index: Long
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
