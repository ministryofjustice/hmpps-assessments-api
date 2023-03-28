package uk.gov.justice.digital.assessments.services

import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.restclient.CommunityApiRestClient
import uk.gov.justice.digital.assessments.restclient.DeliusIntegrationRestClient
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Address
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.CaseDetails
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.MainOffence
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Name
import uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Type
import java.time.LocalDate

@ExtendWith(MockKExtension::class)
@DisplayName("Offender Service Tests")
class OffenderServiceTest {

  private val communityApiRestClient: CommunityApiRestClient = mockk()
  private val deliusIntegrationRestClient: DeliusIntegrationRestClient = mockk()
  private val offenderService: OffenderService = OffenderService(communityApiRestClient, deliusIntegrationRestClient)

  private val crn = "DX12340A"
  private val eventId = 1L

  @BeforeEach
  fun setup() {
    every { deliusIntegrationRestClient.getCaseDetails(crn, eventId) } returns caseDetails()
  }

  @Test
  fun `return offender`() {
    val offenderDto = offenderService.getOffender(crn, eventId)

    assertThat(offenderDto.crn).isEqualTo(crn)
    assertThat(offenderDto.firstName).isEqualTo("forename")
    assertThat(offenderDto.surname).isEqualTo("surname")
    verify(exactly = 1) { deliusIntegrationRestClient.getCaseDetails(crn, eventId) }
  }

  private fun caseDetails(): CaseDetails {
    return CaseDetails(
      crn = "DX12340A",
      name = Name(
        forename = "forename",
        middleName = "middleName",
        surname = "surname"
      ),
      dateOfBirth = LocalDate.of(1989, 1, 1),
      genderIdentity = "PREFER TO SELF DESCRIBE",

      mainAddress = Address(
        buildingName = "HMPPS Digital Studio",
        addressNumber = "32",
        district = "Sheffield City Centre",
        county = "South Yorkshire",
        postcode = "S3 7BS",
        town = "Sheffield"
      ),
      sentence = uk.gov.justice.digital.assessments.restclient.deliusintegrationapi.Sentence(
        startDate = LocalDate.of(2020, 2, 1),
        mainOffence = MainOffence(
          category = Type(
            code = "Code",
            description = "Code description"
          ),
          subCategory = Type(
            code = "Sub code",
            description = "Sub code description"
          )
        )
      )
    )
  }
}
