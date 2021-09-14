package uk.gov.justice.digital.assessments.services

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.api.Score
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.repositories.assessments.AssessmentRepository
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PredictorSubType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ScoreLevel
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.math.BigDecimal
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
class AssessmentUpdateServiceITTest() : IntegrationTest() {
  @Autowired
  internal lateinit var assessmentUpdateService: AssessmentUpdateService

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
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `Trying to push update assessment to OASys`() {
    val assessment = assessmentRepository.findByAssessmentUuid(UUID.fromString("29c8d211-68dc-4692-a6e2-d58468127356"))
    val assessmentEpisode = assessment?.episodes?.first()
    assertThat(assessmentEpisode).isNotNull
    val updateAssessmentResponse =
      assessmentUpdateService.updateEpisode(assessmentEpisode!!, UpdateAssessmentEpisodeDto(mutableMapOf()))
    assertThat(updateAssessmentResponse).isEqualTo(
      AssessmentEpisodeDto.from(
        assessmentEpisode,
        null,
        emptyList()
      )
    )
  }

  @Test
  @Transactional("assessmentsTransactionManager")
  fun `Trying to push assessment completion to OASys`() {
    val final = true
    val assessment = assessmentRepository.findByAssessmentUuid(UUID.fromString("29c8d211-68dc-4692-a6e2-d58468127356"))
    val assessmentEpisode = assessment?.episodes?.get(1)
    assertThat(assessmentEpisode).isNotNull
    assessRisksAndNeedsApiMockServer.stubGetRSRPredictorsForOffenderAndOffencesWithCurrentOffences(
      final,
      assessmentEpisode?.episodeUuid!!,
      "X1349"
    )

    val updateAssessmentResponse =
      assessmentUpdateService.closeEpisode(assessmentEpisode!!)
    assertThat(updateAssessmentResponse).isEqualTo(
      AssessmentEpisodeDto.from(
        assessmentEpisode!!,
        null,
        listOf(
          PredictorScoresDto(
            type = PredictorType.RSR,
            scores = mapOf(
              PredictorSubType.RSR.name to Score(
                level = ScoreLevel.HIGH.name,
                score = BigDecimal("11.34"),
                isValid = true,
                date = "2021-08-09 14:46:48"
              ),
              PredictorSubType.OSPC.name to Score(
                level = ScoreLevel.NOT_APPLICABLE.name,
                score = BigDecimal("0"),
                isValid = false,
                date = "2021-08-09 14:46:48"
              ),
              PredictorSubType.OSPI.name to Score(
                level = ScoreLevel.NOT_APPLICABLE.name,
                score = BigDecimal("0"),
                isValid = false,
                date = "2021-08-09 14:46:48"
              ),
            )
          )
        )
      )
    )
  }

}
