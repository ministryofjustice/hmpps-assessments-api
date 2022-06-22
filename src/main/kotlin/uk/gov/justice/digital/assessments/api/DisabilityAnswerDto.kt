package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusDisabilityDto
import uk.gov.justice.digital.assessments.restclient.communityapi.Provision
import java.time.LocalDate

class DisabilityAnswerDto(
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

    fun from(deliusDisabilities: List<DeliusDisabilityDto>): List<DisabilityAnswerDto> {
      return if (deliusDisabilities.isEmpty()) emptyList()
      else deliusDisabilities.map { from(it) }
    }

    private fun whereActive(provision: Provision): Boolean {
      val now = LocalDate.now()
      return (provision.startDate.isBefore(now) || provision.startDate.isEqual(now)) &&
        (provision.finishDate == null || provision.finishDate.isAfter(now))
    }

    fun from(deliusDisability: DeliusDisabilityDto): DisabilityAnswerDto {
      return DisabilityAnswerDto(
        code = deliusDisability.disabilityType.code,
        description = deliusDisability.disabilityType.description,
        notes = deliusDisability.notes,
        adjustments = deliusDisability.provisions?.filter(this::whereActive)?.map { it.provisionType?.description }
      )
    }
  }
}
