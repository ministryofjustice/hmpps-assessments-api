package uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi

import java.time.LocalDate
import java.time.LocalDateTime

data class OffenderAndOffencesDto(
  val crn: String,
  val gender: Gender,
  val dob: LocalDate,
  val assessmentDate: LocalDateTime,
  val currentOffence: CurrentOffence,
  val dateOfFirstSanction: String,
  val totalOffences: Int,
  val totalViolentOffences: Int,
  val dateOfCurrentConviction: String,
  val hasAnySexualOffences: Boolean,
  val isCurrentSexualOffence: Boolean,
  val isCurrentOffenceVictimStranger: Boolean,
  val mostRecentSexualOffenceDate: String,
  val totalSexualOffencesInvolvingAnAdult: Int,
  val totalSexualOffencesInvolvingAChild: Int,
  val totalSexualOffencesInvolvingChildImages: Int,
  val totalNonSexualOffences: Int,
  val earliestReleaseDate: String,
  val hasCompletedInterview: Boolean,
  val dynamicScoringOffences: DynamicScoringOffences? = null
)

data class DynamicScoringOffences(
  val hasSuitableAccommodation: String?,
  val employment: String?,
  val currentRelationshipWithPartner: String?,
  val evidenceOfDomesticViolence: Boolean,
  val isVictim: Boolean,
  val isPerpetrator: Boolean,
  val alcoholUseIssues: String?,
  val bingeDrinkingIssues: String?,
  val impulsivityIssues: String?,
  val temperControlIssues: String?,
  val proCriminalAttitudes: String?,
  val previousOffences: PreviousOffences? = null,
  val currentOffences: CurrentOffences? = null
)

data class CurrentOffences(
  val firearmPossession: Boolean,
  val offencesWithWeapon: Boolean
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
