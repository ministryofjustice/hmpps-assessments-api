package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.communityapi.DeliusPersonalCircumstanceDto

class CarerCommitmentsAnswerDto(
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

    fun from(carerCommitment: DeliusPersonalCircumstanceDto): CarerCommitmentsAnswerDto {
      return CarerCommitmentsAnswerDto(
        code = carerCommitment.personalCircumstanceType.code,
        description = carerCommitment.personalCircumstanceType.description,
        subType = carerCommitment.personalCircumstanceSubType.description,
        subTypeCode = carerCommitment.personalCircumstanceSubType.code,
        notes = carerCommitment.notes,
        isEvidenced = carerCommitment.evidenced,
      )
    }
  }
}
