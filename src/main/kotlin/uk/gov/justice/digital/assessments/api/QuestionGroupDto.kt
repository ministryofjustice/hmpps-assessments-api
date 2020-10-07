package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
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

    @Schema(description = "Display Order for Group", example = "1")
    val displayOrder : String? = null,

    @Schema(description = "Group is Required", example = "mandatory")
    val mandatory : String? = null,

    @Schema(description = "Question Validation for Group", example = "to-do")
    val validation : String? = null,

    @Schema(description = "Questions and Groups")
    val contents : List<GroupContentDto>
): GroupContentDto {
    companion object {
        fun from(group: GroupEntity, questionGroupEntities: Collection<QuestionGroupEntity>): QuestionGroupDto {
            val groupContents =
                    questionGroupEntities.map {
                        if (it.question != null)
                            GroupContentQuestionDto.from(it.question!!, it )
                        else
                            QuestionGroupDto.from(it.nestedGroup!!, it)
                    }

            return QuestionGroupDto(
                    groupId = group.groupUuid,
                    groupCode = group.groupCode,
                    title = group.heading,
                    subheading = group.subheading,
                    helpText = group.helpText,
                    contents = groupContents
            )
        }

        fun from(groupEntity: GroupEntity, parent: QuestionGroupEntity): QuestionGroupDto {
                return QuestionGroupDto(
                        groupId = groupEntity.groupUuid,
                        groupCode = groupEntity.groupCode,
                        title = groupEntity.heading,
                        subheading = groupEntity.subheading,
                        helpText = groupEntity.helpText,
                        displayOrder = parent.displayOrder,
                        mandatory = parent.mandatory,
                        validation = parent.validation,
                        contents = emptyList()
                )
        }
    }
}