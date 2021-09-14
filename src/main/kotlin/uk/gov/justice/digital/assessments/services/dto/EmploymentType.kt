package uk.gov.justice.digital.assessments.services.dto

enum class EmploymentType(val value: String? = null, val oasysValue: Int? = null) {
  NO("no", 0), NOT_AVAILABLE_FOR_WORK("not available for work", 0), YES("yes", 1), MISSING;

  companion object {
    fun fromString(enumValue: String?): EmploymentType {
      return values().firstOrNull { it.value == enumValue }
        ?: MISSING
    }
  }
}