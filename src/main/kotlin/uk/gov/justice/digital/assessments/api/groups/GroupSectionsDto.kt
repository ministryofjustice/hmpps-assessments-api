package uk.gov.justice.digital.assessments.api.groups

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import java.util.UUID

data class GroupSectionsDto(
  @Schema(description = "Group Identifier", example = "<uuid>")
  val groupId: UUID,

  @Schema(description = "Group Code", example = "group-code-name")
  val groupCode: String,

  @Schema(description = "Group Title", example = "Some group!")
  val title: String? = null,

  @Schema(description = "Group Subheading", example = "Some group subheading")
  val subheading: String? = null,

  @Schema(description = "Group Help-text", example = "Some group help text")
  val helpText: String? = null,

  @Schema(description = "Questions and Groups")
  val contents: List<GroupSectionsDto>?
) {
  companion object {
    fun from(group: GroupEntity, contents: List<GroupSectionsDto>?): GroupSectionsDto {
      return GroupSectionsDto(
        groupId = group.groupUuid,
        groupCode = group.groupCode,
        title = group.heading,
        subheading = group.subheading,
        helpText = group.helpText,
        contents = contents
      )
    }
  }
}
