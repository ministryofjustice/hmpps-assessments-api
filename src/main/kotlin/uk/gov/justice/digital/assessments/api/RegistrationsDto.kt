package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistration
import java.time.LocalDate

// TODO: populate this list
val flagsToInclude = listOf<String>("IRMO")

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
