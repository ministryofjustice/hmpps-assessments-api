package uk.gov.justice.digital.assessments.restclient.communityapi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
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

@JsonIgnoreProperties(ignoreUnknown = true)
class Provision(
  val notes: String? = null,
  val provisionType: ProvisionType? = null
)

data class ProvisionType(
  val code: String,
  val description: String
)
