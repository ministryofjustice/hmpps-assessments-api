package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import java.util.*

class GroupDto (

        @Schema(description = "Group primary key", example = "1234")
        val groupId: Long,

        @Schema(description = "Group UUID foreign key", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
        val groupUuid: UUID,

        @Schema(description = "Group Heading", example = "Some group heading")
        val heading: String? = null,

        @Schema(description = "Group Subheading", example = "Some group subheading")
        val subheading: String? = null,

        @Schema(description = "Group Help-text", example = "Some group help text")
        val helpText: String? = null
){
    companion object{
        fun from(groupEntity: GroupEntity): GroupDto{
        return GroupDto(
                groupEntity.groupId,
                groupEntity.groupUuid,
                groupEntity.heading,
                groupEntity.subheading,
                groupEntity.helpText )
        }
    }
}
