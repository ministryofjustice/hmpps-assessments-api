package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistration
import java.time.LocalDate

private enum class Flags(val code: String) {
  // included flags
  HATE_CRIME("IRMO"),
  NON_REGISTERED_SEX_OFFENDER("ANSO"),
  REGISTERED_SEX_OFFENDER("ARSO"),
  NOT_MAPPA_ELIGIBLE("NOTMAPPA"),
  PUBLIC_INTEREST_CASE("RPIR"),
  SEXUAL_HARM_PREVENTION_ORDER("SHPO"),
  STREET_GANGS("STRG"),
  SUICIDE_OR_SELF_HARM("ALSH"),
  VULNERABLE("RVLN"),
  WEAPONS("WEAP"),
  MODERN_DAY_SLAVERY_VICTIM("MSV"),
  MODERN_DAY_SLAVERY_PERPETRATOR("MSP"),
  DOMESTIC_ABUSE_PERPETRATOR("ADVP"),
  DOMESTIC_ABUSE_VICTIM("ADVV"),

  // excluded flags
  ALERT_NOTICE("ALERT"),
  BARRED_WORKING_CHILDREN("ADWC"),
  BARRED_WORKING_ADULTS("BWWA"),
  CHILD_CONCERNS("RCCO"),
  CHILD_PROTECTION("RCPR"),
  CHILD_SEXUAL_EXPLOITATION_PERPETRATOR("CSEP"),
  DOMESTIC_ABUSE_HISTORY("REG30"),
  DUPLICATE_RECORDS_EXIST("DOFF"),
  HIGH_RISK_OF_HARM("RHRH"),
  HOME_OFFICE_INTEREST("HOIE"),
  INTEGRATED_OFFENDER_MANAGEMENT("IIOM"),
  KEEP_IICSA("IKII"),
  KNOWN_DUPLICATE_RECORD("KDUP"),
  LIFER("INLL"),
  LOW_ROH("RLRH"),
  MAPPA("MAPP"),
  MARAC("IMAR"),
  MEDIUM_RISK_OF_HARM("RMRH"),
  MENTAL_HEALTH_ISSUES("AMHL"),
  MENTALLY_DISORDERED_OFFENDER("RMDO"),
  ORGANISED_CRIME("REG26"),
  PPO("APPO"),
  RECORD_TO_BE_RETAINED("IKMF"),
  REQUIRED_RISK_REVIEW("RRR"),
  RISK_TO_CHILDREN("RCHD"),
  RISK_TO_KNOWN_ADULT("REG15"),
  RISK_TO_PRISONER("REG16"),
  RISK_TO_PUBLIC("REG17"),
  RISK_TO_STAFF("AV2S2"),
  RISK_TO_ADULT_AT_RISK("REG22"),
  SFO("ASFO"),
  TERRORISM_ACT_OFFENDER("RTAO"),
  VERY_HIGH_RISK_OF_HARM("RVHR"),
  VISOR("AVIS"),
  VICTIM_CONTACT("INVI"),
  WARRANT_SUMMONS("WRSM"),
  WOMENS_SAFETY_WORKER("REG24"),
  RESTRAINING_ORDER("RSTO"),
  SAFEGUARDING_ADULT_AT_RISK("RVAD"),
  CUCKOOING_POTENTIAL_VICTIM("CUCK"),
  STALKING_PROTECTION_ORDER("SPO"),
  DORIS("DORIS"),
  CORRUPTOR("COR"),
}

private val flagsToExclude = listOf(
  Flags.ALERT_NOTICE,
  Flags.BARRED_WORKING_CHILDREN,
  Flags.BARRED_WORKING_ADULTS,
  Flags.CHILD_CONCERNS,
  Flags.CHILD_PROTECTION,
  Flags.CHILD_SEXUAL_EXPLOITATION_PERPETRATOR,
  Flags.DOMESTIC_ABUSE_HISTORY,
  Flags.DUPLICATE_RECORDS_EXIST,
  Flags.HIGH_RISK_OF_HARM,
  Flags.HOME_OFFICE_INTEREST,
  Flags.INTEGRATED_OFFENDER_MANAGEMENT,
  Flags.KEEP_IICSA,
  Flags.KNOWN_DUPLICATE_RECORD,
  Flags.LIFER,
  Flags.LOW_ROH,
  Flags.MAPPA,
  Flags.MARAC,
  Flags.MEDIUM_RISK_OF_HARM,
  Flags.MENTAL_HEALTH_ISSUES,
  Flags.MENTALLY_DISORDERED_OFFENDER,
  Flags.ORGANISED_CRIME,
  Flags.PPO,
  Flags.RECORD_TO_BE_RETAINED,
  Flags.REQUIRED_RISK_REVIEW,
  Flags.RISK_TO_CHILDREN,
  Flags.RISK_TO_KNOWN_ADULT,
  Flags.RISK_TO_PRISONER,
  Flags.RISK_TO_PUBLIC,
  Flags.RISK_TO_STAFF,
  Flags.RISK_TO_ADULT_AT_RISK,
  Flags.SFO,
  Flags.TERRORISM_ACT_OFFENDER,
  Flags.VERY_HIGH_RISK_OF_HARM,
  Flags.VISOR,
  Flags.VICTIM_CONTACT,
  Flags.WARRANT_SUMMONS,
  Flags.WOMENS_SAFETY_WORKER,
  Flags.RESTRAINING_ORDER,
  Flags.SAFEGUARDING_ADULT_AT_RISK,
  Flags.CUCKOOING_POTENTIAL_VICTIM,
  Flags.STALKING_PROTECTION_ORDER,
  Flags.DORIS,
  Flags.CORRUPTOR,
).map { flag -> flag.code }

data class RegistrationsDto(
  @Schema(description = "Mappa level and category", example = "{}")
  val mappa: Mappa? = null,

  @Schema(description = "Flags", example = "[]")
  val flags: List<Flag> = emptyList(),
) {
  companion object {
    fun from(registrations: List<CommunityRegistration>): RegistrationsDto {
      val mappa = registrations.firstOrNull { it.type.code == "MAPP" }

      return RegistrationsDto(
        mappa?.let { Mappa(it.registerLevel?.code, it.registerLevel?.description, it.registerCategory?.code, it.registerCategory?.description, it.startDate) },
        registrations
          .filter { it.active && it.type.code !in flagsToExclude }
          .map { Flag(it.type.code, it.type.description, it.riskColour) }
      )
    }
  }
}

class Flag(
  val code: String,
  val description: String,
  val colour: String,
)

class Mappa(
  val level: String?,
  val levelDescription: String?,
  val category: String?,
  val categoryDescription: String?,
  val startDate: LocalDate?,
)
