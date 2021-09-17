package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenceDto.Companion.getMainOffence
import java.time.LocalDate

data class OffenceDto(
  @Schema(description = "Offence category code")
  val offenceCode: String? = null,

  @Schema(description = "Description for offence category code")
  val codeDescription: String? = null,

  @Schema(description = "Offence sub-category code")
  val offenceSubCode: String? = null,

  @Schema(description = "Description for offence sub-category code")
  val subCodeDescription: String? = null,

  @Schema(description = "Sentence start date for conviction")
  val sentenceDate: LocalDate? = null
) {

  companion object {
    fun from(convictionDto: CommunityConvictionDto): OffenceDto {
      val offence = getMainOffence(convictionDto.offences)
      return OffenceDto(
        offenceCode = offence.detail?.mainCategoryCode,
        codeDescription = offence.detail?.mainCategoryDescription,
        offenceSubCode = offence.detail?.subCategoryCode,
        subCodeDescription = offence.detail?.subCategoryDescription,
        sentenceDate = convictionDto.sentence?.startDate
      )
    }

    fun from(offenceEntity: OffenceEntity?): OffenceDto {
      return OffenceDto(
        offenceCode = offenceEntity?.offenceCode,
        codeDescription = offenceEntity?.codeDescription,
        offenceSubCode = offenceEntity?.offenceSubCode,
        subCodeDescription = offenceEntity?.subCodeDescription,
        sentenceDate = offenceEntity?.sentenceDate
      )
    }
  }
}
