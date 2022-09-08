package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
import uk.gov.justice.digital.assessments.api.DeliusEventType
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData
import java.util.UUID

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
class AssessmentServiceITTest : IntegrationTest() {
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
    MDC.put(RequestData.USER_FULL_NAME_HEADER, "Stuart Withlam")
    MDC.put(RequestData.USER_AUTH_SOURCE_HEADER, "delius")
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `Should create assessment`() {

    val crn = "X1356"
    val assessmentResponse =
      assessmentService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = 1L,
          crn = crn,
          assessmentSchemaCode = AssessmentType.UPW
        )
      )
    assertThat(assessmentResponse.assessmentUuid).isNotNull
    assertThat(assessmentResponse.subject).isNotNull
    assertThat(assessmentResponse.subject?.crn).isEqualTo(crn)

    val assessmentEntity = assessmentRepository.findByAssessmentUuid(assessmentResponse.assessmentUuid)
    assertThat(assessmentEntity).isNotNull
    assertThat(assessmentEntity?.assessmentUuid).isEqualTo(assessmentResponse.assessmentUuid)
    assertThat(assessmentEntity?.subject).isNotNull
    assertThat(assessmentEntity?.subject?.crn).isEqualTo(crn)
    assertThat(assessmentEntity?.episodes).hasSize(1)
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `Should create assessment with Delius Conviction ID`() {

    val crn = "X1356"
    val assessmentResponse =
      assessmentService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = 123456L,
          crn = crn,
          assessmentSchemaCode = AssessmentType.UPW,
          deliusEventType = DeliusEventType.EVENT_ID
        )
      )
    assertThat(assessmentResponse.assessmentUuid).isNotNull
    assertThat(assessmentResponse.subject).isNotNull
    assertThat(assessmentResponse.subject?.crn).isEqualTo(crn)

    val assessmentEntity = assessmentRepository.findByAssessmentUuid(assessmentResponse.assessmentUuid)
    assertThat(assessmentEntity?.assessmentUuid).isEqualTo(assessmentResponse.assessmentUuid)
    assertThat(assessmentEntity?.subject?.crn).isEqualTo(crn)
    assertThat(assessmentEntity?.episodes).hasSize(1)
    assertThat(assessmentEntity?.episodes?.get(0)?.offence?.sourceId).isEqualTo("123456")
  }

  @Test
  fun `Trying to create assessment twice only creates one assessment and returns previously created second time`() {

    val crn = "X1356"
    val assessmentResponse =
      assessmentService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = 1L,
          crn = crn,
          assessmentSchemaCode = AssessmentType.UPW
        )
      )
    val assessmentUuid = assessmentResponse.assessmentUuid
    assertThat(assessmentUuid).isNotNull
    assertThat(assessmentResponse.subject).isNotNull
    assertThat(assessmentResponse.subject?.crn).isEqualTo(crn)

    val assessmentSecondResponse =
      assessmentService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = 1L,
          crn = crn,
          assessmentSchemaCode = AssessmentType.UPW
        )
      )

    assertThat(assessmentSecondResponse.assessmentUuid).isEqualTo(assessmentUuid)
    assertThat(assessmentResponse.subject).isNotNull
    assertThat(assessmentResponse.subject?.crn).isEqualTo(crn)
  }

  @Test
  fun `Trying to create assessment with invalid LAO access throws error `() {

    val crn = "OX123456"
    val exception = assertThrows<ExternalApiForbiddenException> {
      assessmentService.createNewAssessment(
        CreateAssessmentDto(
          deliusEventId = 1L,
          crn = crn,
          assessmentSchemaCode = AssessmentType.UPW
        )
      )
    }
    assertThat(exception.moreInfo).containsAll(listOf("excluded", "restricted"))
  }

  @Test
  fun `Trying to get assessment with invalid LAO access throws error `() {

    val exception = assertThrows<ExternalApiForbiddenException> {
      assessmentService.getAssessmentByUuid(UUID.fromString("6e60784e-584e-4762-952d-d7288e31d4f4"))
    }
    assertThat(exception.moreInfo).containsAll(listOf("excluded", "restricted"))
  }
}
