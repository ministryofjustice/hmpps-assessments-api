package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.dto.ScoreType
import java.math.BigDecimal

data class PredictorScoresDto(
  @Schema(description = "Predictor type", example = "RSR")
  val type: PredictorType,

  @Schema(description = "Predictor type", example = "RSR")
  val scoreType: ScoreType,

  @Schema(description = "Predictor scores")
  val scores: Map<String, Score>,
)

data class Score(
  val level: String?,
  val score: BigDecimal?,
  val isValid: Boolean,
  val date: String
)
