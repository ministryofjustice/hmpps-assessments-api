package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistration
import java.time.LocalDate

private enum class Flags(val code: String) {
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
}

// TODO: populate this list
private val flagsToInclude = listOf(
  Flags.HATE_CRIME,
  Flags.NON_REGISTERED_SEX_OFFENDER,
  Flags.NOT_MAPPA_ELIGIBLE,
  Flags.PUBLIC_INTEREST_CASE,
  Flags.REGISTERED_SEX_OFFENDER,
  Flags.SEXUAL_HARM_PREVENTION_ORDER,
  Flags.STREET_GANGS,
  Flags.SUICIDE_OR_SELF_HARM,
  Flags.VULNERABLE,
  Flags.WEAPONS,
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
          .filter { it.active && flagsToInclude.contains(it.type.code) }
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
