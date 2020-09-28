package uk.gov.justice.digital.assessments.api

import io.swagger.annotations.ApiModel

@ApiModel(description = "Create a new Assessment request")
data class CreateAssessmentDto(
        val supervisionId: String? = null
)
