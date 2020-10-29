package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import java.util.UUID

data class AnswerDto (

        @Schema(description = "Free text answer", example = "Some random text")
        val freeTextAnswer: String?,

        //this could be a set of Answer Schema DTO
        val answers: Map<UUID, String> = emptyMap()
)
{
        companion object {
                fun from(answers: MutableMap<UUID, AnswerEntity>?): Map<UUID, AnswerDto>? {
                        return answers?.mapValues { AnswerDto(it.value.freeTextAnswer, it.value.answers) }
                }
        }
}
