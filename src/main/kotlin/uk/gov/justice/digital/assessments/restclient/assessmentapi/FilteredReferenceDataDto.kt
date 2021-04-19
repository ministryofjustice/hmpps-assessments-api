package uk.gov.justice.digital.assessments.restclient.assessmentapi

import java.util.*

data class FilteredReferenceDataDto(
  val oasysSetPk: Long,
  val oasysUserCode: String,
  val oasysAreaCode: String,
  val offenderPk: Long?,
  val assessmentType: String,
  val sectionCode: String,
  val fieldName: String,
  val parentList: Map<String, String>?
)
