package uk.gov.justice.digital.needs.services

class NeedConfiguration(
  val harmQuestion: String,
  val reoffendingQuestion: String,
  val lowScoreNeedQuestion: String,
  val threshold: Int,
  val thresholdQuestions: Set<String> // threshold questions are ints, values from a likert scale
)
