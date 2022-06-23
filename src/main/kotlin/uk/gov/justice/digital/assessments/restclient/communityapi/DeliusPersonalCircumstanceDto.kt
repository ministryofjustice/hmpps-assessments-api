package uk.gov.justice.digital.assessments.restclient.communityapi

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeliusPersonalCircumstanceDto(
  val personalCircumstanceType: PersonalCircumstanceType,
  val personalCircumstanceSubType: PersonalCircumstanceType,
  val notes: String? = null,
  val evidenced: Boolean,
  val isActive: Boolean,
)

data class PersonalCircumstanceType(
  val code: String,
  val description: String,
)
