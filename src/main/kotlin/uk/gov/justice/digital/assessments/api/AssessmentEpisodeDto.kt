package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentEpisodeEntity
import java.time.LocalDateTime
import java.util.UUID

class AssessmentEpisodeDto(

  @Schema(description = "Episode primary key", example = "1234")
  val episodeId: Long? = null,

  @Schema(description = "Episode UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val episodeUuid: UUID? = null,

  @Schema(description = "Assessment UUID foreign key", example = "1234")
  val assessmentUuid: UUID? = null,

  @Schema(description = "Associated OASys assessment ID (OASysSetPK)", example = "1234")
  val oasysAssessmentId: Long? = null,

  @Schema(description = "Reason for Change", example = "CHANGE_OF_ADDRESS")
  val reasonForChange: String? = null,

  @Schema(description = "Episode start timestamp", example = "2020-01-02T16:00:00")
  val created: LocalDateTime? = null,

  @Schema(description = "Episode end timestamp", example = "2020-01-02T16:00:00")
  val ended: LocalDateTime? = null,

  @Schema(description = "Answers associated with this episode")
  val answers: Map<UUID, AnswerDto> = emptyMap()
) {
  companion object {

    fun from(episodes: MutableCollection<AssessmentEpisodeEntity>): Collection<AssessmentEpisodeDto> {
      return episodes.mapNotNull { from(it) }.toSet()
    }

    fun from(episode: AssessmentEpisodeEntity): AssessmentEpisodeDto {
      return AssessmentEpisodeDto(
        episode.episodeId,
        episode.episodeUuid,
        episode.assessment?.assessmentUuid,
        episode.oasysSetPk,
        episode.changeReason,
        episode.createdDate,
        episode.endDate,
        AnswerDto.from(episode.answers) ?: emptyMap()
      )
    }
  }
}
