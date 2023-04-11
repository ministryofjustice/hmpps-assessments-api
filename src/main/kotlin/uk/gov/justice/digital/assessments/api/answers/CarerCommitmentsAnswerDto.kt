package uk.gov.justice.digital.assessments.api.answers

import com.fasterxml.jackson.annotation.JsonProperty
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.PersonalCircumstance

data class CarerCommitmentsAnswerDto(
  @JsonProperty("description")
  val description: String,

  @JsonProperty("code")
  val code: String,

  @JsonProperty("subType")
  val subType: String?,

  @JsonProperty("subTypeCode")
  val subTypeCode: String?,

  @JsonProperty("notes")
  val notes: String? = null,

  @JsonProperty("isEvidenced")
  val isEvidenced: Boolean,
) {
  companion object {

    fun from(carerCommitments: List<PersonalCircumstance>?): List<CarerCommitmentsAnswerDto> {
      return if (carerCommitments?.isNullOrEmpty() == true) {
        emptyList()
      } else {
        carerCommitments.map { from(it) }
      }
    }

    fun from(deliusPersonalCircumstanceDto: PersonalCircumstance): CarerCommitmentsAnswerDto {
      return CarerCommitmentsAnswerDto(
        code = deliusPersonalCircumstanceDto.type.code,
        description = deliusPersonalCircumstanceDto.type.description,
        subType = deliusPersonalCircumstanceDto.subType?.description,
        subTypeCode = deliusPersonalCircumstanceDto.subType?.code,
        notes = deliusPersonalCircumstanceDto.notes,
        isEvidenced = deliusPersonalCircumstanceDto.evidenced,
      )
    }
  }
}
