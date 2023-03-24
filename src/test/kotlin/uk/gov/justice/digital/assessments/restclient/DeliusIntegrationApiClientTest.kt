package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDate

class DeliusIntegrationApiClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var deliusIntegrationRestClient: DeliusIntegrationRestClient

  @Nested
  @DisplayName("get Delius case details")
  inner class GetDeliusOffenderDetails {

    private val crn = "DX5678A"
    private val eventId = 123456L

    @Test
    fun `get Delius Case Details returns CaseDetails DTO`() {
      val caseDetailsDto = deliusIntegrationRestClient.getCaseDetails(crn, eventId)
      assertThat(caseDetailsDto?.crn).isEqualTo("DX5678A")
      assertThat(caseDetailsDto?.pncNumber).isEqualTo("A/1234560BA")
    }

    @Test
    fun `get Delius Offender returns not found`() {
      assertThrows<ExternalApiEntityNotFoundException> {
        deliusIntegrationRestClient.getCaseDetails("invalidNotFound", eventId)
      }
    }

    @Test
    fun `get Delius Offender returns bad request`() {
      assertThrows<ExternalApiInvalidRequestException> {
        deliusIntegrationRestClient.getCaseDetails("invalidBadRequest", eventId)
      }
    }

    @Test
    fun `get Delius Offender returns unauthorised`() {
      assertThrows<ExternalApiAuthorisationException> {
        deliusIntegrationRestClient.getCaseDetails("invalidUnauthorized", eventId)
      }
    }

    @Test
    fun `get Delius Offender returns forbidden`() {
      assertThrows<ExternalApiForbiddenException> {
        deliusIntegrationRestClient.getCaseDetails("invalidForbidden", eventId)
      }
    }

    @Test
    fun `get Delius Offender returns unknown exception`() {
      assertThrows<ExternalApiUnknownException> {
        deliusIntegrationRestClient.getCaseDetails("invalidNotKnow", eventId)
      }
    }

    @Test
    fun `get Delius Offender returns offender DTO with aliases`() {
      val caseDetailsDto = deliusIntegrationRestClient.getCaseDetails(crn, eventId)
      assertThat(caseDetailsDto?.name?.forename).isEqualTo("John")
      assertThat(caseDetailsDto?.name?.surname).isEqualTo("Smith")
      assertThat(caseDetailsDto?.aliases?.get(0)?.name?.forename).isEqualTo("John")
      assertThat(caseDetailsDto?.aliases?.get(0)?.name?.surname).isEqualTo("Smithy")
      assertThat(caseDetailsDto?.aliases?.get(1)?.name?.forename).isEqualTo("Jonny")
      assertThat(caseDetailsDto?.aliases?.get(1)?.name?.surname).isEqualTo("Smith")
    }

    @Test
    fun `get Delius Convictions returns conviction DTO`() {
      val caseDetailsDto = deliusIntegrationRestClient.getCaseDetails(crn, eventId)
      assertThat(caseDetailsDto?.sentence?.mainOffence?.category?.code).isEqualTo("150")
      assertThat(caseDetailsDto?.sentence?.mainOffence?.category?.description).isEqualTo("Merchant Shipping Acts")
      assertThat(caseDetailsDto?.sentence?.mainOffence?.subCategory?.code).isEqualTo("00")
      assertThat(caseDetailsDto?.sentence?.mainOffence?.subCategory?.description).isEqualTo("Merchant Shipping Acts")
      assertThat(caseDetailsDto?.sentence?.startDate).isEqualTo(LocalDate.of(2023, 1, 26))
    }
  }
}
