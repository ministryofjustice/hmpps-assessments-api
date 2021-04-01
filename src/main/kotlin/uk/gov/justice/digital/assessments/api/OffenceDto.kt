package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.Offence
import java.time.LocalDate
import javax.persistence.EntityNotFoundException

class OffenceDto(
  val convictionId: Long? = null,
  val convictionDate: LocalDate? = null,
  val mainOffenceId: String? = null,
  val offenceCode: String? = null,
  val offenceDescription: String? = null,
  val categoryCode: String? = null,
  val categoryDescription: String? = null,
  val subCategoryCode: String? = null,
  val subCategoryDescription: String? = null
) {
  companion object {
    fun from(convictionDto: CommunityConvictionDto): OffenceDto {
      val offence = getMainOffence(convictionDto.offences)
      return OffenceDto(
        convictionId = convictionDto.convictionId,
        convictionDate = convictionDto.convictionDate,
        mainOffenceId = offence.offenceId,
        offenceCode = offence.detail?.code,
        offenceDescription = offence.detail?.description,
        categoryCode = offence.detail?.mainCategoryCode,
        categoryDescription = offence.detail?.mainCategoryDescription,
        subCategoryCode = offence.detail?.subCategoryCode,
        subCategoryDescription = offence.detail?.subCategoryDescription
      )
    }

    private fun getMainOffence(offences: List<Offence>?): Offence {
      if (offences.isNullOrEmpty()) {
        throw EntityNotFoundException("No offences found")
      } else {
        return offences.first { it.mainOffence == true }
      }
    }
  }
}
