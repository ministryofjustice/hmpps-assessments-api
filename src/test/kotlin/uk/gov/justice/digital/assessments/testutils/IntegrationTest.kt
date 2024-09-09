package uk.gov.justice.digital.assessments.testutils

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import uk.gov.justice.digital.assessments.HmppsAssessmentApiApplication
import uk.gov.justice.digital.assessments.JwtAuthHelper
import java.time.Duration

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@ContextConfiguration
@SpringBootTest(
  classes = [HmppsAssessmentApiApplication::class],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ActiveProfiles(profiles = ["test"])
abstract class IntegrationTest {

  var session: MockHttpSession? = null
  var request: MockHttpServletRequest? = null

  @Autowired
  internal lateinit var webTestClient: WebTestClient

  @Autowired
  internal lateinit var jwtHelper: JwtAuthHelper

  companion object {
    internal val deliusIntegrationMockServer = DeliusIntegrationMockServer()
    internal val assessmentApiMockServer = AssessmentApiMockServer()
    internal val assessRisksAndNeedsApiMockServer = AssessRisksAndNeedsApiMockServer()
    internal val auditApiMockServer = AuditMockServer()
    internal val oauthMockServer = OAuthMockServer()

    @BeforeAll
    @JvmStatic
    fun startMocks() {
      assessmentApiMockServer.start()
      assessRisksAndNeedsApiMockServer.start()
      auditApiMockServer.start()
      oauthMockServer.start()
      deliusIntegrationMockServer.start()
    }

    @AfterAll
    @JvmStatic
    fun stopMocks() {
      assessmentApiMockServer.stop()
      assessRisksAndNeedsApiMockServer.stop()
      auditApiMockServer.stop()
      oauthMockServer.stop()
      deliusIntegrationMockServer.stop()
    }
  }

  init {
    SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("user", "pw")
    // Resolves an issue where Wiremock keeps previous sockets open from other tests causing connection resets
    System.setProperty("http.keepAlive", "false")
  }

  @BeforeEach
  fun resetStubs() {
    deliusIntegrationMockServer.resetAll()
    assessmentApiMockServer.stubGetAssessment()
    assessRisksAndNeedsApiMockServer.resetAll()
    auditApiMockServer.stubAuditEvents()
    oauthMockServer.stubGrantToken()
    deliusIntegrationMockServer.stubGetCaseData()
    deliusIntegrationMockServer.stubGetUserAccess()
    RequestContextHolder.getRequestAttributes()
    startSession()
    startRequest()
  }

  @AfterEach
  fun resetRedis() {
    endRequest()
    endSession()
  }

  fun startSession() {
    session = MockHttpSession()
  }

  fun endSession() {
    session!!.clearAttributes()
    session = null
  }

  fun startRequest() {
    request = MockHttpServletRequest()
    request!!.session = session
    RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
  }

  fun endRequest() {
    (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).requestCompleted()
    RequestContextHolder.resetRequestAttributes()
    request = null
  }

  internal fun setAuthorisation(
    user: String = "offender-assessment-api",
    fullName: String = "Full Name",
    roles: List<String> = listOf("ROLE_PROBATION"),
  ): (HttpHeaders) -> Unit {
    val token = jwtHelper.createJwt(
      subject = user,
      fullName = fullName,
      scope = listOf("read"),
      expiryTime = Duration.ofHours(1L),
      roles = roles,
    )

    return { it.set(HttpHeaders.AUTHORIZATION, "Bearer $token") }
  }
}
