package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusDisabilityDto

class DisabilityAnswerDto(

  @JsonProperty("disability_notes")
  val notes: String? = null,

  @JsonProperty("disability_adjustments")
  val adjustments: List<String?>? = null,

) {
  companion object {

    fun from(deliusDisabilities: List<DeliusDisabilityDto>): List<DisabilityAnswerDto> {
      return deliusDisabilities.map { from(it) }
    }

    fun from(deliusDisability: DeliusDisabilityDto): DisabilityAnswerDto {
      return DisabilityAnswerDto(
        notes = deliusDisability.notes,
        adjustments = deliusDisability.provisions?.map { it.provisionType?.description }
      )
    }
  }
}
