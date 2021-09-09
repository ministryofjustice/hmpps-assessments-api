package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import java.util.UUID

data class CheckboxGroupDto(
  @Schema(description = "Checkbox Group Identifier", example = "<uuid>")
  val checkboxGroupId: UUID,

  @Schema(description = "Checkbox Code", example = "Checkbox-code-name")
  val checkboxGroupCode: String,

  @Schema(description = "Checkbox Title", example = "Checkbox of children")
  val title: String? = null,

  @Schema(description = "Checkbox Subheading", example = "Some group subheading")
  val subheading: String? = null,

  @Schema(description = "Checkbox Help-text", example = "Some group help text")
  val helpText: String? = null,

  @Schema(description = "Display Order for Checkbox", example = "1")
  val displayOrder: Int? = 0,

  @Schema(description = "Checkbox is Required", example = "true")
  val mandatory: Boolean? = null,

  @Schema(description = "Question Validation for Checkbox", example = "to-do")
  val validation: String? = null,

  @Schema(description = "Questions and Groups")
  val contents: List<GroupContentDto>
) : GroupContentDto {
  companion object {
    fun from(group: GroupEntity, contents: List<GroupContentDto>, parentGroup: QuestionGroupEntity? = null): CheckboxGroupDto {
      return CheckboxGroupDto(
        checkboxGroupId = group.groupUuid,
        checkboxGroupCode = group.groupCode,
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
