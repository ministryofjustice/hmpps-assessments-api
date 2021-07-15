package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType

class PredictorScoreDto(
  @Schema(description = "Predictor type", example = "RSR")
  val predictor: PredictorType,

  @Schema(description = "Predictor score", example = "1")
  val score: Number? = null,
) {
  companion object {
    fun conditionsNotMet(predictor: PredictorType): PredictorScoreDto {
      return PredictorScoreDto(predictor)
    }
  }
}
