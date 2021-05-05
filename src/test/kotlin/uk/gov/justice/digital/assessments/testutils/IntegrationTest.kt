package uk.gov.justice.digital.assessments.testutils

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.assessments.HmppsAssessmentApiApplication
import uk.gov.justice.digital.assessments.JwtAuthHelper
import java.time.Duration

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(classes = [HmppsAssessmentApiApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
abstract class IntegrationTest {

  @Autowired
  internal lateinit var webTestClient: WebTestClient

  @Autowired
  internal lateinit var jwtHelper: JwtAuthHelper

  companion object {
    internal val courtCaseMockServer = CourtCaseMockServer()
    internal val assessmentUpdateMockServer = AssessmentUpdateMockServer()
    internal val communityApiMockServer = CommunityApiMockServer()
    internal val assessmentApiMockServer = AssessmentApiMockServer()

    @BeforeAll
    @JvmStatic
    fun startMocks() {
      courtCaseMockServer.start()
      assessmentUpdateMockServer.start()
      communityApiMockServer.start()
      assessmentApiMockServer.start()
    }

    @AfterAll
    @JvmStatic
    fun stopMocks() {
      courtCaseMockServer.stop()
      assessmentUpdateMockServer.stop()
      communityApiMockServer.stop()
      assessmentApiMockServer.stop()
    }
  }

  init {
    SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("user", "pw")
    // Resolves an issue where Wiremock keeps previous sockets open from other tests causing connection resets
    System.setProperty("http.keepAlive", "false")
  }

  @BeforeEach
  fun resetStubs() {
    courtCaseMockServer.resetAll()
    courtCaseMockServer.stubCourtCase()
    assessmentUpdateMockServer.stubCreateOffender()
    communityApiMockServer.resetAll()
    communityApiMockServer.stubGetOffender()
    communityApiMockServer.stubGetConvictions()
    assessmentApiMockServer.stubGetAssessment()
  }

  internal fun setAuthorisation(user: String = "offender-assessment-api", roles: List<String> = listOf()): (HttpHeaders) -> Unit {
    val token = jwtHelper.createJwt(
      subject = user,
      scope = listOf("read"),
      expiryTime = Duration.ofHours(1L),
      roles = roles
    )
    return { it.set(HttpHeaders.AUTHORIZATION, "Bearer $token") }
  }
}
