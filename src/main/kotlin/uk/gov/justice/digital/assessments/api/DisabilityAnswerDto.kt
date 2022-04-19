package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.restclient.communityapi.Disability

data class DisabilityAnswerDto(
  val code: String? = null,
  val description: String? = null,
  val notes: String? = null,
  val provisions: List<ProvisionDto> = emptyList()
) {
  companion object {
    fun from(disabilities: List<Disability>): List<DisabilityAnswerDto> {
      return disabilities.map { from(it) }
    }

    fun from(disability: Disability): DisabilityAnswerDto {
      return DisabilityAnswerDto(
        code = disability.disabilityType.code,
        description = disability.disabilityType.description,
        notes = disability.notes,
        provisions = disability.provisions.map {
          ProvisionDto(
            code = it.provisionType.code,
            description = it.provisionType.description
          )
        }
      )
    }
  }
}

data class ProvisionDto(
  val code: String? = null,
  val description: String? = null
)
