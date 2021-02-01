package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import java.util.UUID

class UpdateAssessmentEpisodeDto(

  @Schema(description = "Answers associated with this episode")
  val answers: Map<UUID, AnswerDto>

)
