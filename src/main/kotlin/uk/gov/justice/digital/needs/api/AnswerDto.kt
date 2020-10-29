package uk.gov.justice.digital.needs.api

import io.swagger.v3.oas.annotations.media.Schema

data class AnswerDto (

        @Schema(description = "Answer ID and value", example = "<RSR01, yes>")
        val answers: Map<String, String> = emptyMap()

        //todo add date that answer was last updated and episode id for answer?
)