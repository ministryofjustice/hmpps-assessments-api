package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.PredictorType

enum class PredictorResultStatus {
  DETERMINED,
  UNDETERMINED,
  FAILED,
}

data class PredictorScoreDto(
  @Schema(description = "Predictor type", example = "RSR")
  val type: PredictorType,

  @Schema(description = "Predictor result status", example = "COMPLETE")
  val status: PredictorResultStatus = PredictorResultStatus.DETERMINED,

  @Schema(description = "Predictor score", example = "1")
  val score: Number? = null,
) {
  companion object {
    fun incomplete(predictor: PredictorType): PredictorScoreDto {
      return PredictorScoreDto(
        type = predictor,
        status = PredictorResultStatus.UNDETERMINED
      )
    }
  }
}
