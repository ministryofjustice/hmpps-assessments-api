package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.GroupSummaryEntity
import java.util.*

class GroupSummaryDto(
    @Schema(description = "Group Identifier", example = "<uuid>")
    val groupId : UUID,

    @Schema(description = "Group Title", example = "Some group!")
    val title: String,

    @Schema(description = "Total number of groups and questions this group contains", example = "2")
    val contentCount: Long,

    @Schema(description = "Number of child groups", example = "3")
    val groupCount: Long,

    @Schema(description = "Number of question in this group", example = "5")
    val questionCount: Long
) {
    companion object {
        fun from(entity: GroupSummaryEntity): GroupSummaryDto {
            return GroupSummaryDto(
                    UUID.fromString(entity.groupUuid),
                    entity.heading,
                    entity.contentCount,
                    entity.groupCount,
                    entity.questionCount
            )
        }
    }
}
