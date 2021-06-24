package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponseOffender(
  @Schema(required = true, description = "Fullname of the offender", example = "Ray Arnold")
  val name: String? = null,

  @Schema(required = false, description = "Offender PNC", example = "1234/123456A")
  val pnc: String? = null,
)
