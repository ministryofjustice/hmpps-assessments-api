package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.util.UUID

data class GroupWithContentsDto(

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

  @Schema(description = "Display Order for Group", example = "1")
  val displayOrder: Int? = 0,

  @Schema(description = "Group is Required", example = "true")
  val mandatory: Boolean? = null,

  @Schema(description = "Question Validation for Group", example = "to-do")
  val validation: String? = null,

  @Schema(description = "Questions and Groups")
  val contents: List<GroupContentDto>
) : GroupContentDto {
  companion object {
    fun from(group: GroupEntity, contents: List<GroupContentDto>, parentGroup: QuestionGroupEntity? = null): GroupWithContentsDto {
      return GroupWithContentsDto(
        groupId = group.groupUuid,
        groupCode = group.groupCode,
        title = group.heading,
        subheading = group.subheading,
        helpText = group.helpText,
        displayOrder = parentGroup?.displayOrder,
        mandatory = parentGroup?.mandatory,
        validation = parentGroup?.validation,
        contents = contents
      )
    }
  }
}
