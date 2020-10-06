package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonUnwrapped
import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.QuestionGroupEntity
import java.time.LocalDateTime
import java.util.*

data class QuestionGroupDto (

    @Schema(description = "Group Identifier", example = "<uuid>")
    val groupId : UUID,

    @Schema(description = "Reference Questions")
    val questionRefs : List<GetQuestionsForGroupDto>,

    @Schema(description = "Reference Group")
    val group : GroupDto,

    @Schema(description = "Question Group Start Date", example = "2020-01-02T16:00:00")
    val groupStart : LocalDateTime? = null,

    @Schema(description = "Question Group End Date", example = "2020-01-02T16:00:00")
    val groupEnd : LocalDateTime? = null
) {
    companion object {

        fun from(questionGroupEntities: Collection<QuestionGroupEntity>): QuestionGroupDto {
            val questionGroup = questionGroupEntities.elementAt(0)
            val questionRefs = questionGroupEntities.map {
                        GetQuestionsForGroupDto.from(it.questionSchema, questionGroup )}
            return QuestionGroupDto(
                    groupId = questionGroup.group.groupUuid,
                    questionRefs = questionRefs,
                    group = GroupDto.from(questionGroup.group)
            )
        }
    }
}