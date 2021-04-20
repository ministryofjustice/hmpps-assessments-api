package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

data class CreateOffenderDto(val crn: String, val areaCode: String, val oasysUserCode: String, val deliusEvent: Long?)
