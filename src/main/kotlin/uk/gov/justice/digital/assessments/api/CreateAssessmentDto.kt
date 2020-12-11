package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema

data class CreateAssessmentDto(
        @Schema(description = "Supervision ID", example = "CRN1")
        val supervisionId: String? = null,

        @Schema(description = "Court Code", example = "SHF")
        val courtCode: String? = null,
        @Schema(description = "Case Number", example = "1234567890")
        val caseNumber: String? = null
)
