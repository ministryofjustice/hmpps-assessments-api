package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import uk.gov.justice.digital.assessments.restclient.AssessRisksAndNeedsApiRestClient
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistration
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistrationElement
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistrations
import java.time.LocalDate

class RisksServiceTest {
  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val assessRisksAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient = mockk()
  private val risksService = RisksService(communityApiRestClient, assessRisksAndNeedsApiRestClient)

  private val crn = "A123456"

  @BeforeEach
  private fun setup() {
  }

  @Nested
  @DisplayName("get offender registrations")
  inner class GetOffenderRegistrations {
    @Test
    fun `handles when there are no registrations`() {
      every { communityApiRestClient.getRegistrations(crn) } returns CommunityRegistrations(emptyList())

      val registrations = risksService.getRegistrationsForAssessment(crn)

      assertThat(registrations.mappa).isNull()
      assertThat(registrations.flags.size).isEqualTo(0)
    }

    @Test
    fun `returns MAPPA information when available`() {
      every { communityApiRestClient.getRegistrations(crn) } returns CommunityRegistrations(
        listOf(
          CommunityRegistration(
            active = true,
            warnUser = true,
            riskColour = "Red",
            registerCategory = CommunityRegistrationElement("M2", "MAPPA Cat 2"),
            registerLevel = CommunityRegistrationElement("M1", "MAPPA Level 1"),
            type = CommunityRegistrationElement("MAPP", "MAPPA"),
            startDate = LocalDate.parse("2021-10-10"),
          ),
        )
      )

      val registrations = risksService.getRegistrationsForAssessment(crn)

      assertThat(registrations.mappa).isNotNull
      assertThat(registrations.mappa?.level).isEqualTo("M1")
      assertThat(registrations.mappa?.levelDescription).isEqualTo("MAPPA Level 1")
      assertThat(registrations.mappa?.category).isEqualTo("M2")
      assertThat(registrations.mappa?.categoryDescription).isEqualTo("MAPPA Cat 2")
      assertThat(registrations.mappa?.startDate).isEqualTo(LocalDate.parse("2021-10-10"))
    }

    @Test
    fun `handles when there is no MAPPA information`() {
      every { communityApiRestClient.getRegistrations(crn) } returns CommunityRegistrations(
        listOf(
          CommunityRegistration(
            active = true,
            warnUser = true,
            riskColour = "Red",
            registerCategory = CommunityRegistrationElement("RC12", "Hate Crime - Disability"),
            type = CommunityRegistrationElement("IRMO", "Hate Crime"),
            startDate = LocalDate.parse("2021-10-10"),
          ),
        )
      )

      val registrations = risksService.getRegistrationsForAssessment(crn)

      assertThat(registrations.mappa).isNull()
    }

    @Test
    fun `returns active and non-excluded registrations as flags`() {
      every { communityApiRestClient.getRegistrations(crn) } returns CommunityRegistrations(
        listOf(
          CommunityRegistration(
            active = true,
            warnUser = true,
            riskColour = "Red",
            registerCategory = CommunityRegistrationElement("RC12", "Hate Crime - Disability"),
            type = CommunityRegistrationElement("IRMO", "Hate Crime"),
            startDate = LocalDate.parse("2021-10-10"),
          ),
          CommunityRegistration(
            active = true,
            warnUser = true,
            riskColour = "Red",
            registerCategory = CommunityRegistrationElement("M2", "MAPPA Cat 2"),
            registerLevel = CommunityRegistrationElement("M1", "MAPPA Level 1"),
            type = CommunityRegistrationElement("MAPP", "MAPPA"),
            startDate = LocalDate.parse("2021-10-10"),
          ),
        )
      )

      val registrations = risksService.getRegistrationsForAssessment(crn)

      assertThat(registrations.flags.size).isEqualTo(1)
      assertThat(registrations.flags.first().code).isEqualTo("IRMO")
      assertThat(registrations.flags.first().description).isEqualTo("Hate Crime")
      assertThat(registrations.flags.first().colour).isEqualTo("Red")
    }

    @Test
    fun `returns active registrations when added to community API but not in excluded list`() {
      every { communityApiRestClient.getRegistrations(crn) } returns CommunityRegistrations(
        listOf(
          CommunityRegistration(
            active = true,
            warnUser = true,
            riskColour = "Red",
            registerCategory = CommunityRegistrationElement("RC12", "Hate Crime - Disability"),
            type = CommunityRegistrationElement("IRMO", "Hate Crime"),
            startDate = LocalDate.parse("2021-10-10"),
          ),
          CommunityRegistration(
            active = true,
            warnUser = true,
            riskColour = "Green",
            registerCategory = CommunityRegistrationElement("T2", "TEST CATEGORY"),
            registerLevel = CommunityRegistrationElement("T1", "TEST Level"),
            type = CommunityRegistrationElement("TEST", "TEST REGISTRATION"),
            startDate = LocalDate.parse("2021-10-10"),
          ),
        )
      )

      val registrations = risksService.getRegistrationsForAssessment(crn)

      assertThat(registrations.flags.size).isEqualTo(2)
      assertThat(registrations.flags.first().code).isEqualTo("IRMO")
      assertThat(registrations.flags.first().description).isEqualTo("Hate Crime")
      assertThat(registrations.flags.first().colour).isEqualTo("Red")
      assertThat(registrations.flags[1].code).isEqualTo("TEST")
      assertThat(registrations.flags[1].description).isEqualTo("TEST REGISTRATION")
      assertThat(registrations.flags[1].colour).isEqualTo("Green")
    }
  }
  @Nested
  @DisplayName("get ROSH risk summary")
  inner class GetRoshRiskSummary {
    @Test
    fun `fetchs the ROSH risk summary`() {
      every { assessRisksAndNeedsApiRestClient.getRoshRiskSummary(crn) } returns RoshRiskSummaryDto(
        overallRisk = "HIGH",
        lastUpdated = LocalDate.parse("2021-10-10"),
        riskToChildrenInCommunity = "LOW",
        riskToKnownAdultInCommunity = "MEDIUM",
        riskToStaffInCommunity = "MEDIUM",
        riskToPublicInCommunity = "HIGH"
      )

      val riskSummary = risksService.getRoshRiskSummaryForAssessment(crn)

      assertThat(riskSummary.overallRisk).isEqualTo("HIGH")
      assertThat(riskSummary.lastUpdated).isEqualTo(LocalDate.parse("2021-10-10"))
      assertThat(riskSummary.riskToChildrenInCommunity).isEqualTo("LOW")
      assertThat(riskSummary.riskToKnownAdultInCommunity).isEqualTo("MEDIUM")
      assertThat(riskSummary.riskToStaffInCommunity).isEqualTo("MEDIUM")
      assertThat(riskSummary.riskToPublicInCommunity).isEqualTo("HIGH")
    }

    @Test
    fun `handles when risk is not known`() {
      every { assessRisksAndNeedsApiRestClient.getRoshRiskSummary(crn) } returns RoshRiskSummaryDto(
        overallRisk = "HIGH",
        lastUpdated = LocalDate.parse("2021-10-10"),
        riskToChildrenInCommunity = null,
        riskToKnownAdultInCommunity = null,
        riskToStaffInCommunity = null,
        riskToPublicInCommunity = null,
      )

      val riskSummary = risksService.getRoshRiskSummaryForAssessment(crn)

      assertThat(riskSummary.overallRisk).isEqualTo("HIGH")
      assertThat(riskSummary.riskToChildrenInCommunity).isEqualTo(null)
      assertThat(riskSummary.riskToKnownAdultInCommunity).isEqualTo(null)
      assertThat(riskSummary.riskToStaffInCommunity).isEqualTo(null)
      assertThat(riskSummary.riskToPublicInCommunity).isEqualTo(null)
    }
  }
}
