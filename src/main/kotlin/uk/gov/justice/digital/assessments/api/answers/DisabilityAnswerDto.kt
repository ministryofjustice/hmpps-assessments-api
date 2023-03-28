package uk.gov.justice.digital.assessments.api.answers

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Disability

data class DisabilityAnswerDto(
  @JsonProperty("code")
  val code: String? = null,

  @JsonProperty("description")
  val description: String? = null,

  @JsonProperty("disability_notes")
  val notes: String? = null,

  @JsonProperty("disability_adjustments")
  val adjustments: List<String?>? = null,
) {
  companion object {

    fun from(deliusDisability: Disability): DisabilityAnswerDto {
      return DisabilityAnswerDto(
        code = deliusDisability.type.code,
        description = deliusDisability.type.description,
        notes = deliusDisability.notes,
        adjustments = deliusDisability.provisions
      )
    }
  }
}
