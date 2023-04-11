package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import java.time.LocalDate

data class RegistrationsDto(
  @Schema(description = "Mappa level and category", example = "{}")
  val mappa: Mappa? = null,

  @Schema(description = "Flags", example = "[]")
  val flags: List<Flag> = emptyList(),
) {
  companion object {
    fun from(caseDetails: CaseDetails): RegistrationsDto {
      return RegistrationsDto(
        caseDetails.mappaRegistration?.let {
          Mappa(
            it.level.code,
            it.level.description,
            it.category.code,
            it.category.description,
            it.startDate,
          )
        },
        caseDetails.registerFlags.orEmpty().map { Flag(it.code, it.description, it.riskColour.orEmpty()) },
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
