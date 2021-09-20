package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEpisodeEntity
import java.time.LocalDateTime
import java.util.UUID

data class OasysAssessmentEpisodeDto(

  @Schema(description = "Episode UUID", example = "0e5e0848-6ab0-4b1b-a354-f7894913d8e4")
  val episodeUuid: UUID? = null,

  @Schema(description = "Assessment where an episode belongs to")
  val assessment: AssessmentDto? = null,

  @Schema(description = "Associated OASys assessment ID (OASysSetPK)", example = "1234")
  val oasysAssessmentId: Long? = null,

  @Schema(description = "Episode start timestamp", example = "2020-01-02T16:00:00")
  val created: LocalDateTime? = null,

  @Schema(description = "Episode end timestamp", example = "2020-01-02T16:00:00")
  val ended: LocalDateTime? = null,

  @Schema(description = "Oasys Answers associated with this episode")
  val answers: EpisodeOasysAnswersDto,
) {
  companion object {
    fun from(
      episode: AssessmentEpisodeEntity,
      answers: EpisodeOasysAnswersDto
    ): OasysAssessmentEpisodeDto {
      return OasysAssessmentEpisodeDto(
        episode.episodeUuid,
        episode.assessment.toAssessmentDto(),
        episode.oasysSetPk,
        episode.createdDate,
        episode.endDate,
        answers
      )
    }
    private fun AssessmentEntity?.toAssessmentDto(): AssessmentDto? {
      return this?.let { AssessmentDto(it.assessmentUuid, it.createdDate, it.completedDate) }
    }
  }
}
