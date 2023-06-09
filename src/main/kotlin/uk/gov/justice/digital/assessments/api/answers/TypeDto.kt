package uk.gov.justice.digital.assessments.api.answers

import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Type

data class TypeDto(
  val code: String,
  val description: String,
) {
  companion object {
    fun from(type: Type) = TypeDto(type.code, type.description)
  }
}
