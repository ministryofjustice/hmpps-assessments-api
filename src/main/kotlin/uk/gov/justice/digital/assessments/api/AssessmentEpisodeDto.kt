package uk.gov.justice.digital.assessments.api

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import java.time.LocalDateTime

@ApiModel(description = "Assessment Episode of Change Model")
class AssessmentEpisodeDto (

        @ApiModelProperty(value = "Episode primary key", example = "1234")
    val episodeId: Long? = null,

        @ApiModelProperty(value = "Assessment primary key", example = "1234")
    val assessmentId: Long? = null,

        @ApiModelProperty(value = "Reason for Change", example = "CHANGE_OF_ADDRESS")
    val reasonForChange: String? = null,

        @ApiModelProperty(value = "Episode start timestamp")
    val created: LocalDateTime? = null,

        @ApiModelProperty(value = "Episode end timestamp")
    val ended: LocalDateTime? = null,

        @ApiModelProperty(value = "Answers associated with this episode")
    val answers: Set<Any> = mutableSetOf()

) {
    companion object {

        fun from(episodes: MutableCollection<AssessmentEpisodeEntity>): Collection<AssessmentEpisodeDto> {
            return episodes.mapNotNull { from(it) }.toSet()
        }

        fun from(episode: AssessmentEpisodeEntity?): AssessmentEpisodeDto? {
            if(episode == null) return null
            return AssessmentEpisodeDto(
                    episode.episodeId,
                    episode.assessment?.assessmentId,
                    episode.changeReason,
                    episode.createdDate,
                    episode.endDate,
                    emptySet()

            )
        }
    }

}
