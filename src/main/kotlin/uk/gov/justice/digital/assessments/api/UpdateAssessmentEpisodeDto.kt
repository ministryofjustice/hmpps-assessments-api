package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import java.util.UUID

class UpdateAssessmentEpisodeDto(
  @Schema(description = "Answers associated with this episode")
  val answers: Map<UUID, Collection<Answer>>
)
