package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.assessments.OffenceEntity
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Sentence
import java.time.LocalDate

data class OffenceDto(

  @Schema(description = "Conviction ID")
  val convictionId: Long? = null,

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
    fun from(sentence: Sentence?, eventId: Long?): OffenceDto? {
      return if (sentence == null) null
      else OffenceDto(
        convictionId = eventId,
        offenceCode = sentence.mainOffence.category.code,
        codeDescription = sentence.mainOffence.category.description,
        offenceSubCode = sentence.mainOffence.subCategory.code,
        subCodeDescription = sentence.mainOffence.subCategory.description,
        sentenceDate = sentence.startDate
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
