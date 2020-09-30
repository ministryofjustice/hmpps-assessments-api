package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import java.time.LocalDateTime
import java.util.*

class AssessmentEpisodeDto(

        @Schema(description = "Episode primary key", example = "1234")
        val episodeId: Long? = null,

        @Schema(description = "Episode UUID", example = "1234")
        val episodeUuid: UUID? = null,

        @Schema(description = "Assessment UUID foreign key", example = "1234")
        val assessmentUuid: UUID? = null,

        @Schema(description = "Reason for Change", example = "CHANGE_OF_ADDRESS")
        val reasonForChange: String? = null,

        @Schema(description = "Episode start timestamp")
        val created: LocalDateTime? = null,

        @Schema(description = "Episode end timestamp")
        val ended: LocalDateTime? = null,

        @Schema(description = "Answers associated with this episode")
        val answers: Set<Any> = mutableSetOf()

) {
    companion object {

        fun from(episodes: MutableCollection<AssessmentEpisodeEntity>): Collection<AssessmentEpisodeDto> {
            return episodes.mapNotNull { from(it) }.toSet()
        }

        fun from(episode: AssessmentEpisodeEntity?): AssessmentEpisodeDto? {
            if (episode == null) return null
            return AssessmentEpisodeDto(
                    episode.episodeId,
                    episode.episodeUuid,
                    episode.assessment?.assessmentUuid,
                    episode.changeReason,
                    episode.createdDate,
                    episode.endDate,
                    emptySet()

            )
        }
    }

}
