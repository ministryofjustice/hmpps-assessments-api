package uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi

import java.time.LocalDate
import java.time.LocalDateTime

data class OffenderAndOffencesDto(
  val crn: String,
  val gender: Gender,
  val dob: LocalDate,
  val assessmentDate: LocalDateTime,
  val currentOffence: CurrentOffence,
  val dateOfFirstSanction: LocalDate,
  val totalOffences: Int,
  val totalViolentOffences: Int,
  val dateOfCurrentConviction: LocalDate,
  val hasAnySexualOffences: Boolean,
  val isCurrentSexualOffence: Boolean,
  val isCurrentOffenceVictimStranger: Boolean,
  val mostRecentSexualOffenceDate: LocalDate,
  val totalSexualOffencesInvolvingAnAdult: Int,
  val totalSexualOffencesInvolvingAChild: Int,
  val totalSexualOffencesInvolvingChildImages: Int,
  val totalNonSexualOffences: Int,
  val earliestReleaseDate: LocalDate,
  val hasCompletedInterview: Boolean,
  val dynamicScoringOffences: DynamicScoringOffences?
)

data class DynamicScoringOffences(
  val committedOffenceUsingWeapon: Boolean,
  val hasSuitableAccommodation: ProblemsLevel?,
  val employment: EmploymentType?,
  val currentRelationshipWithPartner: ProblemsLevel?,
  val evidenceOfDomesticViolence: Boolean,
  val isAVictim: Boolean,
  val isAPerpetrator: Boolean,
  val alcoholUseIssues: ProblemsLevel?,
  val bingeDrinkingIssues: ProblemsLevel?,
  val impulsivityIssues: ProblemsLevel?,
  val temperControlIssues: ProblemsLevel?,
  val proCriminalAttitudes: ProblemsLevel?,
  val previousOffences: PreviousOffences?
)

data class PreviousOffences(
  val murderAttempt: Boolean,
  val wounding: Boolean,
  val aggravatedBurglary: Boolean,
  val arson: Boolean,
  val criminalDamage: Boolean,
  val kidnapping: Boolean,
  val firearmPossession: Boolean,
  val robbery: Boolean,
  val offencesWithWeapon: Boolean
)

data class CurrentOffence(val offenceCode: String, val offenceSubcode: String)

enum class EmploymentType {
  NO, NOT_AVAILABLE_FOR_WORK, YES, MISSING
}

enum class Gender {
  MALE, FEMALE
}

enum class ProblemsLevel(val score: Int? = null) {
  NO_PROBLEMS, SOME_PROBLEMS, SIGNIFICANT_PROBLEMS, MISSING
}
