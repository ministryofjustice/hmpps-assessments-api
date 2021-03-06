package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.util.UUID

data class TableQuestionDto(
  @Schema(description = "Table Identifier", example = "<uuid>")
  val tableId: UUID,

  @Schema(description = "Table Code", example = "table-code-name")
  val tableCode: String,

  @Schema(description = "Table Title", example = "Table of children")
  val title: String? = null,

  @Schema(description = "Table Subheading", example = "Some group subheading")
  val subheading: String? = null,

  @Schema(description = "Table Help-text", example = "Some group help text")
  val helpText: String? = null,

  @Schema(description = "Display Order for Table", example = "1")
  val displayOrder: Int? = 0,

  @Schema(description = "Table is Required", example = "true")
  val mandatory: Boolean? = null,

  @Schema(description = "Question Validation for Table", example = "to-do")
  val validation: String? = null,

  @Schema(description = "Questions and Groups")
  val contents: List<GroupContentDto>
) : GroupContentDto {
  companion object {
    fun from(group: GroupEntity, contents: List<GroupContentDto>, parentGroup: QuestionGroupEntity? = null): TableQuestionDto {
      return TableQuestionDto(
        tableId = group.groupUuid,
        tableCode = group.groupCode,
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
