package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionGroupEntity
import java.util.UUID

data class TableQuestionDto(
  @Schema(description = "Table Identifier", example = "<uuid>")
  val tableId: UUID? = null,

  @Schema(description = "Table Code", example = "table-code-name")
  val tableCode: String? = null,

  @Schema(description = "Table Title", example = "Table of children")
  val title: String? = null,

  @Schema(description = "Table Subheading", example = "Some group subheading")
  val subheading: String? = null,

  @Schema(description = "Table Help-text", example = "Some group help text")
  val helpText: String? = null,

  @Schema(description = "Questions and Groups")
  val contents: List<GroupContentDto> = mutableListOf()
) : GroupContentDto {
  companion object {
    fun from(group: GroupEntity, contents: List<GroupContentDto>, parentGroup: QuestionGroupEntity? = null): TableQuestionDto {
      return TableQuestionDto(
        tableId = group.groupUuid,
        tableCode = group.groupCode,
        title = group.heading,
        subheading = group.subheading,
        helpText = group.helpText,
        contents = contents
      )
    }
  }
}
