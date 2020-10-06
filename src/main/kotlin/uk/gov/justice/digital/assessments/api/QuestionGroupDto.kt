package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.time.LocalDateTime
import java.util.*

data class QuestionGroupDto (

    @Schema(description = "Group Identifier", example = "<uuid>")
    val groupId : UUID,

    @Schema(description = "Group Code", example="group-code-name")
    val groupCode: String,

    @Schema(description = "Group Title", example = "Some group!")
    val title: String? = null,

    @Schema(description = "Group Subheading", example = "Some group subheading")
    val subheading: String? = null,

    @Schema(description = "Group Help-text", example = "Some group help text")
    val helpText: String? = null,

    @Schema(description = "Questions and Groups")
    val contents : List<GroupContentQuestionDto>,
) {
    companion object {

        fun from(questionGroupEntities: Collection<QuestionGroupEntity>): QuestionGroupDto {
            val questionGroup = questionGroupEntities.elementAt(0)
            val questionRefs = questionGroupEntities.map {
                GroupContentQuestionDto.from(it.questionSchema, questionGroup )}

            val group = questionGroup.group
            return QuestionGroupDto(
                    groupId = group.groupUuid,
                    groupCode = group.groupCode,
                    title = group.heading,
                    subheading = group.subheading,
                    helpText = group.helpText,
                    contents = questionRefs
            )
        }
    }
}