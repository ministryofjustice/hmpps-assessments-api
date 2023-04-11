package uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi

import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.dto.ScoreType
import java.math.BigDecimal

data class RiskPredictorsDto(
  val calculatedAt: String,
  val type: PredictorType,
  val scoreType: ScoreType,
  val scores: Map<String, Score>,
  val errors: List<String> = emptyList(),
)

data class Score(
  val level: ScoreLevel?,
  val score: BigDecimal?,
  val isValid: Boolean,
)

enum class ScoreLevel(val type: String) {
  LOW("Low"), MEDIUM("Medium"), HIGH("High"), VERY_HIGH("Very High"), NOT_APPLICABLE("Not Applicable");
}
