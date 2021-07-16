package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType

enum class PredictorResultStatus {
  DETERMINED,
  UNDETERMINED,
  FAILED,
}

class PredictorScoreDto(
  @Schema(description = "Predictor type", example = "RSR")
  val predictor: PredictorType,

  @Schema(description = "Predictor result status", example = "COMPLETE")
  val predictorResultStatus: PredictorResultStatus = PredictorResultStatus.DETERMINED,

  @Schema(description = "Predictor score", example = "1")
  val score: Number? = null,
) {
  companion object {
    fun incomplete(predictor: PredictorType): PredictorScoreDto {
      return PredictorScoreDto(
        predictor = predictor,
        predictorResultStatus = PredictorResultStatus.UNDETERMINED
      )
    }
  }
}
