package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema

data class ConditionalsSchemaDto(

  @Schema(
    description = "Display this question when this answer is selected",
    example = "question_code"
  )
  val conditional: String? = null,

  @Schema(description = "Should the question triggered by this answer be displayed inline?", example = "<Boolean>")
  val displayInline: Boolean? = true

)
