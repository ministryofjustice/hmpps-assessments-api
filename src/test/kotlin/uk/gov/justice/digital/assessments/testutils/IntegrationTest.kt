package uk.gov.justice.digital.assessments.testutils

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.assessments.HmppsAssessmentApiApplication
import uk.gov.justice.digital.assessments.JwtAuthHelper
import uk.gov.justice.digital.assessments.redis.entities.UserDetails
import java.time.Duration

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest(
  classes = [HmppsAssessmentApiApplication::class],
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = ["test"])
abstract class IntegrationTest {

  @Autowired
  internal lateinit var webTestClient: WebTestClient

  @Autowired
  internal lateinit var redisTemplate: RedisTemplate<String, UserDetails>

  @Autowired
  internal lateinit var jwtHelper: JwtAuthHelper

  companion object {
    internal val courtCaseMockServer = CourtCaseMockServer()
    internal val assessmentUpdateMockServer = AssessmentUpdateMockServer()
    internal val communityApiMockServer = CommunityApiMockServer()
    internal val assessmentApiMockServer = AssessmentApiMockServer()
    internal val assessRisksAndNeedsApiMockServer = AssessRisksAndNeedsApiMockServer()
    internal val auditApiMockServer = AuditMockServer()

    @BeforeAll
    @JvmStatic
    fun startMocks() {
      courtCaseMockServer.start()
      assessmentUpdateMockServer.start()
      communityApiMockServer.start()
      assessmentApiMockServer.start()
      assessRisksAndNeedsApiMockServer.start()
      auditApiMockServer.start()
    }

    @AfterAll
    @JvmStatic
    fun stopMocks() {
      courtCaseMockServer.stop()
      assessmentUpdateMockServer.stop()
      communityApiMockServer.stop()
      assessmentApiMockServer.stop()
      assessRisksAndNeedsApiMockServer.stop()
      auditApiMockServer.stop()
    }
  }

  init {
    SecurityContextHolder.getContext().authentication = TestingAuthenticationToken("user", "pw")
    // Resolves an issue where Wiremock keeps previous sockets open from other tests causing connection resets
    System.setProperty("http.keepAlive", "false")
  }

  @BeforeEach
  fun resetStubs() {
    redisTemplate.opsForValue().set("user:1", UserDetails("STUARTWHITLAM"))
    courtCaseMockServer.resetAll()
    courtCaseMockServer.stubCourtCase()
    assessmentUpdateMockServer.stubCreateOffender()
    communityApiMockServer.resetAll()
    communityApiMockServer.stubGetOffender()
    communityApiMockServer.stubGetOffenderPersonalContacts()
    communityApiMockServer.stubGetOffenderPersonalCircumstances()
    communityApiMockServer.stubGetOffenderPersonalContactsPregnant()
    communityApiMockServer.stubGetOffenderPersonalCircumstancesPregnant()
    communityApiMockServer.stubGetOffenderRegistrations()
    communityApiMockServer.stubGetConvictions()
    communityApiMockServer.stubGetUserAccess()
    communityApiMockServer.stubUploadDocument()
    assessmentApiMockServer.stubGetAssessment()
    assessRisksAndNeedsApiMockServer.resetAll()
    auditApiMockServer.stubAuditEvents()
  }

  @AfterEach
  fun resetRedis() {
    redisTemplate.delete(redisTemplate.keys("*"))
  }

  internal fun setAuthorisation(
    user: String = "offender-assessment-api",
    fullName: String = "Full Name",
    roles: List<String> = listOf("ROLE_PROBATION")
  ): (HttpHeaders) -> Unit {
    val token = jwtHelper.createJwt(
      subject = user,
      fullName = fullName,
      scope = listOf("read"),
      expiryTime = Duration.ofHours(1L),
      roles = roles
    )
    return { it.set(HttpHeaders.AUTHORIZATION, "Bearer $token") }
  }
}
