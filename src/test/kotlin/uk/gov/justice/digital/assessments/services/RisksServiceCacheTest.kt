package uk.gov.justice.digital.assessments.services

import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData

internal class RisksServiceCacheTest(
  @Autowired
  val risksService: RisksService,
) : IntegrationTest() {
  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, USERNAME)
  }

  @BeforeEach
  fun init() {
    val jwt = Jwt.withTokenValue("token")
      .header("alg", "none")
      .claim("sub", "user")
      .build()
    val authorities: Collection<GrantedAuthority> = AuthorityUtils.createAuthorityList("SCOPE_read")
    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, authorities)
    assessRisksAndNeedsApiMockServer.stubGetRoshRiskSummary()
  }

  @Test
  fun `multiple calls to ROSH risk summary are cached when valid response`() {
    val crn = "DX12340A"

    risksService.getRoshRiskSummaryForAssessment(crn)
    risksService.getRoshRiskSummaryForAssessment(crn)

    assessRisksAndNeedsApiMockServer.verify(exactly(1), getRequestedFor(urlEqualTo("/risks/crn/$crn/widget")))
  }

  @Test
  fun `multiple calls to ROSH risk summary are not cached when any exception occurs`() {
    val crn = "invalidNotKnow"

    assertThrows<ExternalApiUnknownException> {
      risksService.getRoshRiskSummaryForAssessment(crn)
    }

    assertThrows<ExternalApiUnknownException> {
      risksService.getRoshRiskSummaryForAssessment(crn)
    }

    assessRisksAndNeedsApiMockServer.verify(exactly(2), getRequestedFor(urlEqualTo("/risks/crn/$crn/widget")))
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `multiple calls to risk registrations are cached when valid response`() {
    val crn = "X1346"

    risksService.getRegistrationsForAssessment(crn, 123456)
    risksService.getRegistrationsForAssessment(crn, 123456)

    deliusIntegrationMockServer.verify(exactly(1), getRequestedFor(urlEqualTo("/case-data/$crn/123456")))
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `multiple calls to risk registrations are not cached when an exception occurs`() {
    val crn = "X1404"

    assertThrows<ExternalApiEntityNotFoundException> {
      risksService.getRegistrationsForAssessment(crn, 123456)
    }

    assertThrows<ExternalApiEntityNotFoundException> {
      risksService.getRegistrationsForAssessment(crn, 123456)
    }

    deliusIntegrationMockServer.verify(exactly(2), getRequestedFor(urlEqualTo("/case-data/$crn/123456")))
  }

  companion object {
    const val USERNAME = "TestUser1"
  }
}
