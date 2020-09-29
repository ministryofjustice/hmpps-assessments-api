package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema

data class CreateAssessmentDto(
        @Schema(description = "Supervision ID", example = "CRN1", required = true)
        val supervisionId: String? = null
)
