package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.Tables
import java.time.LocalDateTime
import java.util.UUID

data class AssessmentEpisodeDto(

  @Schema(description = "Episode UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val episodeUuid: UUID? = null,

  @Schema(description = "Assessment UUID foreign key", example = "1234")
  val assessmentUuid: UUID,

  @Schema(description = "Associated OASys assessment ID (OASysSetPK)", example = "1234")
  val oasysAssessmentId: Long? = null,

  @Schema(description = "Reason for Change", example = "CHANGE_OF_ADDRESS")
  val reasonForChange: String? = null,

  @Schema(description = "Episode start timestamp", example = "2020-01-02T16:00:00")
  val created: LocalDateTime? = null,

  @Schema(description = "Episode end timestamp", example = "2020-01-02T16:00:00")
  val ended: LocalDateTime? = null,

  @Schema(description = "Created/Updated by user", example = "Name surname")
  val userFullName: String? = null,

  @Schema(description = "Answers associated with this episode")
  val answers: AnswersDto = emptyMap(),

  @Schema(description = "Offence codes")
  val offence: OffenceDto,

  @Schema(description = "Tables associated with this episode")
  val tables: Tables? = null,

  @Schema(description = "Date last edited")
  val lastEditedDate: LocalDateTime? = null,

  @Schema(description = "Date closed")
  val closedDate: LocalDateTime? = null,
) {
  companion object {

    fun from(episodes: MutableCollection<AssessmentEpisodeEntity>): Collection<AssessmentEpisodeDto> {
      return episodes.map { from(it) }.toSet()
    }

    fun from(
      episode: AssessmentEpisodeEntity,
    ): AssessmentEpisodeDto {
      return AssessmentEpisodeDto(
        episode.episodeUuid,
        episode.assessment.assessmentUuid,
        episode.oasysSetPk,
        episode.changeReason,
        episode.createdDate,
        episode.endDate,
        episode.author.userFullName,
        episode.answers,
        OffenceDto.from(episode.offence),
        episode.tables,
        episode.lastEditedDate,
        episode.closedDate,
      )
    }
  }
}
