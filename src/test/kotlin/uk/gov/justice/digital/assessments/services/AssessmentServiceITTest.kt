package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.transaction.annotation.Transactional
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData

@SqlGroup(
  Sql(
    scripts = ["classpath:assessments/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)
  ),
  Sql(
    scripts = ["classpath:assessments/after-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
  )
)
@AutoConfigureWebTestClient
class AssessmentServiceITTest() : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentService: AssessmentService

  @Autowired
  internal lateinit var assessmentRepository: AssessmentRepository

  @BeforeEach
  fun init() {
    val jwt = Jwt.withTokenValue("token")
      .header("alg", "none")
      .claim("sub", "user")
      .build()
    val authorities: Collection<GrantedAuthority> = AuthorityUtils.createAuthorityList("SCOPE_read")
    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken(jwt, authorities)
    MDC.put(RequestData.USER_AREA_HEADER, "WWS")
    MDC.put(RequestData.USER_ID_HEADER, "1")
    MDC.put(RequestData.USER_NAME_HEADER, "SWITHLAM")
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `Trying to create assessment and push to OASys`() {

    val crn = "X1356"
    val assessmentResponse =
      assessmentService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = 1L,
          crn = crn,
          assessmentSchemaCode = AssessmentSchemaCode.ROSH
        )
      )
    assertThat(assessmentResponse.assessmentUuid).isNotNull
    assertThat(assessmentResponse.subject).isNotNull
    assertThat(assessmentResponse.subject?.crn).isEqualTo(crn)

    val assessmentEntity = assessmentRepository.findByAssessmentUuid(assessmentResponse.assessmentUuid!!)
    assertThat(assessmentEntity).isNotNull
    assertThat(assessmentEntity?.assessmentUuid).isEqualTo(assessmentResponse.assessmentUuid)
    assertThat(assessmentEntity?.subject).isNotNull
    assertThat(assessmentEntity?.subject?.crn).isEqualTo(crn)
    assertThat(assessmentEntity?.subject?.oasysOffenderPk).isEqualTo(1L)
    assertThat(assessmentEntity?.episodes).hasSize(1)
    assertThat(assessmentEntity?.episodes?.get(0)?.oasysSetPk).isEqualTo(1L)
  }
}
