package uk.gov.justice.digital.assessments.restclient

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
@SpringBootTest( classes = [HmppsAssessmentApiApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = ["test"])
abstract class IntegrationTest {
    companion object {
        internal val courtCaseMockServer = CourtCaseMockServer()

        @BeforeAll
        @JvmStatic
        fun startMocks() {
            courtCaseMockServer.start()
        }

        @AfterAll
        @JvmStatic
        fun stopMocks() {
            courtCaseMockServer.stop()
        }
    }

    init {
        System.setProperty("http.keepAlive", "false")
    }

    @BeforeEach
    fun resetStubs() {
        courtCaseMockServer.resetAll()
        courtCaseMockServer.stubCourtCase()
    }
}