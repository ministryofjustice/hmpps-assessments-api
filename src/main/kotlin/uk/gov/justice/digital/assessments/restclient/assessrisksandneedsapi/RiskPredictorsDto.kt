package uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi

import uk.gov.justice.digital.assessments.services.dto.PredictorType
import java.math.BigDecimal

data class RiskPredictorsDto(
  val calculatedAt: String,
  val type: PredictorType,
  val scoreType: ScoreType?,
  val rsrScore: Score,
  val ospcScore: Score,
  val ospiScore: Score
)

data class Score(
  val level: ScoreLevel?,
  val score: BigDecimal?,
  val isValid: Boolean
)

enum class ScoreLevel(val type: String) {
  LOW("Low"), MEDIUM("Medium"), HIGH("High"), VERY_HIGH("Very High"), NOT_APPLICABLE("Not Applicable");

  companion object {
    fun findByType(type: String): ScoreLevel? {
      return values().firstOrNull { value -> value.type == type }
    }
  }
}

enum class ScoreType(val type: String) {
  STATIC("Static"), DYNAMIC("Dynamic");

  companion object {
    fun findByType(type: String): ScoreType? {
      return values().firstOrNull { value -> value.type == type }
    }
  }
}
