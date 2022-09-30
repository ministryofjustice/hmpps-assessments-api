package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusPersonalCircumstanceDto

data class CarerCommitmentsAnswerDto(
  @JsonProperty("description")
  val description: String,

  @JsonProperty("code")
  val code: String,

  @JsonProperty("subType")
  val subType: String,

  @JsonProperty("subTypeCode")
  val subTypeCode: String,

  @JsonProperty("notes")
  val notes: String? = null,

  @JsonProperty("isEvidenced")
  val isEvidenced: Boolean,
) {
  companion object {

    fun from(carerCommitments: List<DeliusPersonalCircumstanceDto>): List<CarerCommitmentsAnswerDto> {
      return if (carerCommitments.isEmpty()) emptyList()
      else carerCommitments.map { from(it) }
    }

    fun from(deliusPersonalCircumstanceDto: DeliusPersonalCircumstanceDto): CarerCommitmentsAnswerDto {
      return CarerCommitmentsAnswerDto(
        code = deliusPersonalCircumstanceDto.personalCircumstanceType.code,
        description = deliusPersonalCircumstanceDto.personalCircumstanceType.description,
        subType = deliusPersonalCircumstanceDto.personalCircumstanceSubType.description,
        subTypeCode = deliusPersonalCircumstanceDto.personalCircumstanceSubType.code,
        notes = deliusPersonalCircumstanceDto.notes,
        isEvidenced = deliusPersonalCircumstanceDto.evidenced,
      )
    }
  }
}
