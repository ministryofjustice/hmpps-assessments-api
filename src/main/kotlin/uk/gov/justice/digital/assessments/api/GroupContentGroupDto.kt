package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.util.*

data class GroupContentGroupDto(
        @Schema(description = "Group UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
        val groupId: UUID,

        @Schema(description = "Group Code", example = "group")
        val groupCode: String,

        @Schema(description = "Group Heading", example = "Some group heading")
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
        val validation : String? = null

): GroupContentDto {
    val type = "group"

    companion object{
        fun from(groupEntity: GroupEntity, questionGroupEntity: QuestionGroupEntity): GroupContentGroupDto {
            return GroupContentGroupDto(
                    groupId = groupEntity.groupUuid,
                    groupCode = groupEntity.groupCode,
                    title = groupEntity.heading,
                    subheading = groupEntity.subheading,
                    helpText = groupEntity.helpText,
                    displayOrder = questionGroupEntity.displayOrder,
                    mandatory = questionGroupEntity.mandatory,
                    validation = questionGroupEntity.validation,
            ) }
    }
}