package uk.gov.justice.digital.assessments.services.dto

class ExternalSourceQuestionSchemaDto(
  val questionCode: String,
  val externalSource: String,
  val jsonPathField: String,
  val fieldType: String? = null,
  val externalSourceEndpoint: String? = null,
  val mappedValue: String? = null,
  val ifEmpty: Boolean,
  val structuredQuestionCode: String? = null
)
