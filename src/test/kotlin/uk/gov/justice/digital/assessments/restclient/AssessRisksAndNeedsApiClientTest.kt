package uk.gov.justice.digital.assessments.restclient

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDate

class AssessRisksAndNeedsApiClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var assessRiskAndNeedsApiRestClient: AssessRisksAndNeedsApiRestClient

  @BeforeEach
  fun init() {
    val jwt = Jwt.withTokenValue("token")
      .header("alg", "none")
      .claim("sub", "user")
      .build()
    val authorities: Collection<GrantedAuthority> = AuthorityUtils.createAuthorityList("SCOPE_read")
    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, authorities)
  }

  @Nested
  @DisplayName("get ROSH risk summary")
  inner class GetRoshRiskSummary {

    val crn = "DX12340B"

    @Test
    fun `returns registrations`() {
      val response = assessRiskAndNeedsApiRestClient.getRoshRiskSummary(crn)
      assertThat(response?.riskInCommunity?.get("Children")).isEqualTo("LOW")
      assertThat(response?.riskInCommunity?.get("Known Adult")).isEqualTo("MEDIUM")
      assertThat(response?.riskInCommunity?.get("Staff")).isEqualTo("VERY_HIGH")
      assertThat(response?.riskInCommunity?.get("Public")).isEqualTo("HIGH")
      assertThat(response?.assessedOn).isEqualTo(LocalDate.parse("2021-10-10"))
    }

    @Test
    fun `get ROSH risk summary returns not found`() {
      assertThrows<ExternalApiEntityNotFoundException> {
        assessRiskAndNeedsApiRestClient.getRoshRiskSummary("invalidNotFound")
      }
    }

    @Test
    fun `get ROSH risk summary returns bad request`() {
      assertThrows<ExternalApiInvalidRequestException> {
        assessRiskAndNeedsApiRestClient.getRoshRiskSummary("invalidBadRequest")
      }
    }

    @Test
    fun `get ROSH risk summary returns unauthorised`() {
      assertThrows<ExternalApiAuthorisationException> {
        assessRiskAndNeedsApiRestClient.getRoshRiskSummary("invalidUnauthorized")
      }
    }

    @Test
    fun `get ROSH risk summary returns forbidden`() {
      assertThrows<ExternalApiForbiddenException> {
        assessRiskAndNeedsApiRestClient.getRoshRiskSummary("invalidForbidden")
      }
    }

    @Test
    fun `get ROSH risk summary returns unknown exception`() {
      assertThrows<ExternalApiUnknownException> {
        assessRiskAndNeedsApiRestClient.getRoshRiskSummary("invalidNotKnow")
      }
    }
  }
}
