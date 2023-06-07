package uk.gov.justice.digital.assessments.api.answers

import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Disability

data class DisabilityAnswerDto(
  val type: TypeDto,
  val condition: TypeDto,
  val notes: String? = null,
) {
  companion object {
    fun from(disability: Disability) = DisabilityAnswerDto(
      type = TypeDto.from(disability.type),
      condition = TypeDto.from(disability.condition),
      notes = disability.notes,
    )
  }
}
