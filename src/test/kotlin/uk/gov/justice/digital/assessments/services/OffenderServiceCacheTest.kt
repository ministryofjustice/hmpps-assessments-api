package uk.gov.justice.digital.assessments.services

import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData

internal class OffenderServiceCacheTest(
  @Autowired
  val offenderService: OffenderService,
) : IntegrationTest() {
  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, USERNAME)
  }

  @Test
  fun `multiple calls to validate user access for same CRN and user are cached when access is allowed`() {
    val aCRN = "X123456"

    offenderService.validateUserAccess(aCRN)
    offenderService.validateUserAccess(aCRN)

    deliusIntegrationMockServer.verify(exactly(1), getRequestedFor(urlEqualTo("/users/$USERNAME/access/$aCRN")))
  }

  @Test
  fun `multiple calls are not cached when access is not allowed`() {
    val aCRN = "OX123456"

    assertThrows<ExternalApiForbiddenException> {
      offenderService.validateUserAccess(aCRN)
    }

    assertThrows<ExternalApiForbiddenException> {
      offenderService.validateUserAccess(aCRN)
    }

    deliusIntegrationMockServer.verify(exactly(2), getRequestedFor(urlEqualTo("/users/$USERNAME/access/$aCRN")))
  }

  @Test
  fun `multiple calls are not cached when any exception occurs`() {
    val aCRN = "invalidNotKnow"

    MDC.put(RequestData.USER_NAME_HEADER, "user1")

    assertThrows<ExternalApiUnknownException> {
      offenderService.validateUserAccess(aCRN)
    }

    assertThrows<ExternalApiUnknownException> {
      offenderService.validateUserAccess(aCRN)
    }

    deliusIntegrationMockServer.verify(exactly(2), getRequestedFor(urlEqualTo("/users/$USERNAME/access/$aCRN")))
  }

  companion object {
    const val USERNAME = "user1"
  }
}
