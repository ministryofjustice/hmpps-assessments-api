package uk.gov.justice.digital.assessments.restclient.assessmentupdateapi

class ValidationErrorDto(
  val sectionCode: String? = null,
  val logicalPage: Long? = null,
  val questionCode: String? = null,
  val errorCode: String? = null,
  val message: String? = null,
  val assessmentValidationError: Boolean? = true
)
