package uk.gov.justice.digital.assessments.api

import io.swagger.annotations.ApiModel

@ApiModel(description = "Create a new Assessment Episode request")
data class CreateAssessmentEpisodeDto(
        val changeReason: String
)
