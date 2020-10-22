package uk.gov.justice.digital.needs.api

import io.swagger.v3.oas.annotations.media.Schema

// service should accept a map of questions and answers

class CalculateNeedsDto(

        @Schema(description = "Question Ids and their associated answers ")
        val answers: Map<String, AnswerDto> = emptyMap()

// add triggering episode id

) {


}
