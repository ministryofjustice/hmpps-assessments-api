package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.MappaRegistration
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.RegisterFlag
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Type
import java.time.LocalDate

class RisksServiceTest {
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient = mockk()
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient = mockk()

  private val risksService = RisksService(
    assessRisksAndNeedsApiRestClient,
    deliusIntegrationRestClient,
  )

  private val crn = "X1356"

  @Nested
  @DisplayName("get offender registrations")
  inner class GetOffenderRegistrations {
    @Test
    fun `handles when there are no registrations`() {
      every { deliusIntegrationRestClient.getCaseDetails(crn, 123456) } returns CaseDetails(
        crn = crn,
        name = Name(forename = "Dennis", surname = "Nedry"),
        dateOfBirth = LocalDate.of(1969, 1, 1),
        registerFlags = emptyList(),
      )

      val registrations = risksService.getRegistrationsForAssessment(crn, 123456)

      assertThat(registrations.mappa).isNull()
      assertThat(registrations.flags.size).isEqualTo(0)
    }

    @Test
    fun `returns MAPPA information when available`() {
      every { deliusIntegrationRestClient.getCaseDetails(crn, 123456) } returns CaseDetails(
        crn = crn,
        name = Name(forename = "Dennis", surname = "Nedry"),
        dateOfBirth = LocalDate.of(1969, 1, 1),
        mappaRegistration = MappaRegistration(
          startDate = LocalDate.of(2023, 1, 1),
          level = Type("M1", "MAPPA Level 1"),
          category = Type(code = "M2", description = "MAPPA Cat 2"),
        ),
        registerFlags = emptyList(),
      )

      val registrations = risksService.getRegistrationsForAssessment(crn, 123456)

      assertThat(registrations.mappa).isNotNull
      assertThat(registrations.mappa?.level).isEqualTo("M1")
      assertThat(registrations.mappa?.levelDescription).isEqualTo("MAPPA Level 1")
      assertThat(registrations.mappa?.category).isEqualTo("M2")
      assertThat(registrations.mappa?.categoryDescription).isEqualTo("MAPPA Cat 2")
      assertThat(registrations.mappa?.startDate).isEqualTo(LocalDate.parse("2023-01-01"))
    }

    @Test
    fun `handles when there is no MAPPA information`() {
      every { deliusIntegrationRestClient.getCaseDetails(crn, 123456) } returns CaseDetails(
        crn = crn,
        name = Name(forename = "Dennis", surname = "Nedry"),
        dateOfBirth = LocalDate.of(1969, 1, 1),
        registerFlags = listOf(
          RegisterFlag(
            code = "IRMO",
            description = "Hate Crime",
            riskColour = "Red",
          ),
        ),
      )

      val registrations = risksService.getRegistrationsForAssessment(crn, 123456)

      assertThat(registrations.mappa).isNull()
    }

    @Test
    fun `returns registrations as risk flags`() {
      every { deliusIntegrationRestClient.getCaseDetails(crn, 123456) } returns CaseDetails(
        crn = crn,
        name = Name(forename = "Dennis", surname = "Nedry"),
        dateOfBirth = LocalDate.of(1969, 1, 1),
        registerFlags = listOf(
          RegisterFlag(
            code = "IRMO",
            description = "Hate Crime",
            riskColour = "Red",
          ),
        ),
      )

      val registrations = risksService.getRegistrationsForAssessment(crn, 123456)

      assertThat(registrations.flags.size).isEqualTo(1)
      assertThat(registrations.flags.first().code).isEqualTo("IRMO")
      assertThat(registrations.flags.first().description).isEqualTo("Hate Crime")
      assertThat(registrations.flags.first().colour).isEqualTo("Red")
    }
  }

  @Nested
  @DisplayName("get ROSH risk summary")
  inner class GetRoshRiskSummary {
    @Test
    fun `fetchs the ROSH risk summary`() {
      every { assessRisksAndNeedsApiRestClient.getRoshRiskSummary(crn) } returns RoshRiskSummaryDto(
        overallRisk = "HIGH",
        assessedOn = LocalDate.parse("2021-10-10"),
        riskInCommunity = mapOf(
          "Public" to "HIGH",
          "Known Adult" to "MEDIUM",
          "Staff" to "VERY_HIGH",
          "Children" to "LOW",
        ),
      )

      val riskSummary = risksService.getRoshRiskSummaryForAssessment(crn)

      assertThat(riskSummary.overallRisk).isEqualTo("HIGH")
      assertThat(riskSummary.assessedOn).isEqualTo(LocalDate.parse("2021-10-10"))
      assertThat(riskSummary.riskInCommunity["Children"]).isEqualTo("LOW")
      assertThat(riskSummary.riskInCommunity["Known Adult"]).isEqualTo("MEDIUM")
      assertThat(riskSummary.riskInCommunity["Staff"]).isEqualTo("VERY_HIGH")
      assertThat(riskSummary.riskInCommunity["Public"]).isEqualTo("HIGH")
    }

    @Test
    fun `handles when risk is not known`() {
      every { assessRisksAndNeedsApiRestClient.getRoshRiskSummary(crn) } returns RoshRiskSummaryDto(
        assessedOn = LocalDate.parse("2021-10-10"),
        riskInCommunity = mapOf(
          "Public" to null,
          "Known Adult" to null,
          "Staff" to null,
          "Children" to null,
        ),
      )

      val riskSummary = risksService.getRoshRiskSummaryForAssessment(crn)

      assertThat(riskSummary.overallRisk).isEqualTo(null)
      assertThat(riskSummary.riskInCommunity["Children"]).isEqualTo(null)
      assertThat(riskSummary.riskInCommunity["Known Adult"]).isEqualTo(null)
      assertThat(riskSummary.riskInCommunity["Staff"]).isEqualTo(null)
      assertThat(riskSummary.riskInCommunity["Public"]).isEqualTo(null)
    }
  }
}
