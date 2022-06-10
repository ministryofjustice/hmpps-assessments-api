package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusDisabilityDto

class DisabilityAnswerDto(
  @JsonProperty("type")
  val type: String? = null,

  @JsonProperty("description")
  val description: String? = null,

  @JsonProperty("disability_notes")
  val notes: String? = null,

  @JsonProperty("disability_adjustments")
  val adjustments: List<String?>? = null,
) {
  companion object {

    fun from(deliusDisabilities: List<DeliusDisabilityDto>): List<DisabilityAnswerDto> {
      return if (deliusDisabilities.isEmpty()) emptyList()
      else deliusDisabilities.map { from(it) }
    }

    fun from(deliusDisability: DeliusDisabilityDto): DisabilityAnswerDto {
      return DisabilityAnswerDto(
        type = deliusDisability.disabilityType.code,
        description = deliusDisability.disabilityType.description,
        notes = deliusDisability.notes,
        adjustments = deliusDisability.provisions?.map { it.provisionType?.description }
      )
    }
  }
}
