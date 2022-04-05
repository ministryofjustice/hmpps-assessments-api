package uk.gov.justice.digital.assessments.restclient.communityapi

data class DeliusDisabilityDto(
  val disabilityType: DisabilityType,
  val notes: String? = null,
  val provisions: List<Provision>? = null,
  val isActive: Boolean
)

data class DisabilityType(
  val code: String,
  val description: String
)

class Provision(
  val notes: String? = null,
  val provisionType: ProvisionType? = null
)

data class ProvisionType(
  val code: String,
  val description: String
)
