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
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData

internal class RisksServiceCacheTest(
  @Autowired
  val risksService: RisksService
) : IntegrationTest() {
  @BeforeEach
  fun setup() {
    MDC.put(RequestData.USER_NAME_HEADER, Companion.USERNAME)
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
    val aCRN = "DX12340A"

    risksService.getRoshRiskSummaryForAssessment(aCRN)
    risksService.getRoshRiskSummaryForAssessment(aCRN)

    assessRisksAndNeedsApiMockServer.verify(exactly(1), getRequestedFor(urlEqualTo("/risks/crn/$aCRN/summary")))
  }

  @Test
  fun `multiple calls to ROSH risk summary are not cached when any exception occurs`() {
    val aCRN = "invalidNotKnow"

    assertThrows<ExternalApiUnknownException> {
      risksService.getRoshRiskSummaryForAssessment(aCRN)
    }

    assertThrows<ExternalApiUnknownException> {
      risksService.getRoshRiskSummaryForAssessment(aCRN)
    }

    assessRisksAndNeedsApiMockServer.verify(exactly(2), getRequestedFor(urlEqualTo("/risks/crn/$aCRN/summary")))
  }

  @Test
  fun `multiple calls to risk registrations are cached when valid response`() {
    val aCRN = "DX12340A"

    risksService.getRegistrationsForAssessment(aCRN)
    risksService.getRegistrationsForAssessment(aCRN)

    communityApiMockServer.verify(exactly(1), getRequestedFor(urlEqualTo("/secure/offenders/crn/$aCRN/registrations")))
  }

  @Test
  fun `multiple calls to risk registrations are not cached when any exception occurs`() {
    val aCRN = "invalidNotKnow"

    assertThrows<ExternalApiUnknownException> {
      risksService.getRegistrationsForAssessment(aCRN)
    }

    assertThrows<ExternalApiUnknownException> {
      risksService.getRegistrationsForAssessment(aCRN)
    }

    communityApiMockServer.verify(exactly(2), getRequestedFor(urlEqualTo("/secure/offenders/crn/$aCRN/registrations")))
  }

  companion object {
    const val USERNAME = "TestUser1"
  }
}
