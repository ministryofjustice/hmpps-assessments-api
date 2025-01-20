package uk.gov.justice.digital.assessments.services.dto

enum class ProblemsLevel(val value: String? = null, val oasysValue: Int? = null) {
  NO_PROBLEMS("no problems", 0),
  SOME_PROBLEMS("some problems", 1),
  SIGNIFICANT_PROBLEMS("significant problems", 2),
  MISSING,
  ;

  companion object {
    fun fromString(enumValue: String?): ProblemsLevel = values().firstOrNull { it.value == enumValue }
      ?: MISSING
  }
}
