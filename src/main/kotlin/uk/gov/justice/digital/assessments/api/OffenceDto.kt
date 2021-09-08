package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDto.Companion.getMainOffence
import java.time.LocalDate

data class OffenceDto(
  val convictionId: Long? = null,
  val convictionDate: LocalDate? = null,
  val offenceCode: String? = null,
  val codeDescription: String? = null,
  val offenceSubCode: String? = null,
  val subCodeDescription: String? = null
) {

  companion object {
    fun from(convictionDto: CommunityConvictionDto): OffenceDto {
      val offence = getMainOffence(convictionDto.offences)
      return OffenceDto(
        convictionId = convictionDto.convictionId,
        convictionDate = convictionDto.convictionDate,
        offenceCode = offence.detail!!.mainCategoryCode,
        codeDescription = offence.detail.mainCategoryDescription,
        offenceSubCode = offence.detail.subCategoryCode,
        subCodeDescription = offence.detail.subCategoryDescription
      )
    }
  }
}
