package uk.gov.justice.digital.assessments.api.answers

import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Provision

data class ProvisionAnswerDto(
  val type: TypeDto,
  val category: TypeDto,
) {
  companion object {
    fun from(provision: Provision) = ProvisionAnswerDto(
      TypeDto.from(provision.type),
      TypeDto.from(provision.category),
    )
  }
}
