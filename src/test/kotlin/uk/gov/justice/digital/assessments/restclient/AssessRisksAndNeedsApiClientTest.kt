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
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffence
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.CurrentOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.DynamicScoringOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.EmploymentType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Gender
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.OffenderAndOffencesDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PredictorSubType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PreviousOffences
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ProblemsLevel
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.Score
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ScoreLevel
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.dto.ScoreType
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiAuthorisationException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiEntityNotFoundException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiForbiddenException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiInvalidRequestException
import uk.gov.justice.digital.assessments.services.exceptions.ExternalApiUnknownException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

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
    assessRisksAndNeedsApiMockServer.stubGetRoshRiskSummary()
  }

  @Test
  fun `get RSR predictors for offender and offences`() {
    val final = true
    val episodeUuid = UUID.randomUUID()
    assessRisksAndNeedsApiMockServer.stubGetRSRPredictorsForOffenderAndOffencesWithCurrentOffences(
      final,
      episodeUuid,
      "X1345"
    )
    val offenderAndOffencesDto = OffenderAndOffencesDto(
      crn = "X1345",
      gender = Gender.MALE,
      dob = LocalDate.of(2021, 1, 1).minusYears(20),
      assessmentDate = LocalDateTime.of(2021, 1, 1, 0, 0, 0),
      currentOffence = CurrentOffence("138", "00"),
      dateOfFirstSanction = "2020-01-01",
      totalOffences = 10,
      totalViolentOffences = 8,
      dateOfCurrentConviction = "2020-12-18",
      hasAnySexualOffences = true,
      isCurrentSexualOffence = true,
      isCurrentOffenceVictimStranger = true,
      mostRecentSexualOffenceDate = "2020-12-11",
      totalSexualOffencesInvolvingAnAdult = 5,
      totalSexualOffencesInvolvingAChild = 3,
      totalSexualOffencesInvolvingChildImages = 2,
      totalNonContactSexualOffences = 2,
      earliestReleaseDate = "2021-11-01",
      hasCompletedInterview = true,
      dynamicScoringOffences = DynamicScoringOffences(
        hasSuitableAccommodation = ProblemsLevel.MISSING.name,
        employment = EmploymentType.NOT_AVAILABLE_FOR_WORK.name,
        currentRelationshipWithPartner = ProblemsLevel.SIGNIFICANT_PROBLEMS.name,
        evidenceOfDomesticViolence = true,
        isPerpetrator = true,
        alcoholUseIssues = ProblemsLevel.SIGNIFICANT_PROBLEMS.name,
        bingeDrinkingIssues = ProblemsLevel.SIGNIFICANT_PROBLEMS.name,
        impulsivityIssues = ProblemsLevel.SOME_PROBLEMS.name,
        temperControlIssues = ProblemsLevel.SIGNIFICANT_PROBLEMS.name,
        proCriminalAttitudes = ProblemsLevel.SOME_PROBLEMS.name,
        previousOffences = PreviousOffences(
          murderAttempt = true,
          wounding = true,
          aggravatedBurglary = true,
          arson = true,
          criminalDamage = true,
          kidnapping = true,
          firearmPossession = true,
          robbery = true,
          offencesWithWeapon = true
        ),
        currentOffences = CurrentOffences(
          firearmPossession = true,
          offencesWithWeapon = true
        )
      )
    )

    val riskPredictors = assessRiskAndNeedsApiRestClient.getRiskPredictors(
      PredictorType.RSR,
      offenderAndOffencesDto,
      final,
      episodeUuid
    )
    assertThat(riskPredictors).isEqualTo(
      RiskPredictorsDto(
        type = PredictorType.RSR,
        scoreType = ScoreType.STATIC,
        scores = mapOf(
          PredictorSubType.RSR to Score(level = ScoreLevel.HIGH, score = BigDecimal("11.34"), isValid = true),
          PredictorSubType.OSPC to Score(level = ScoreLevel.NOT_APPLICABLE, score = BigDecimal("0"), isValid = false),
          PredictorSubType.OSPI to Score(level = ScoreLevel.NOT_APPLICABLE, score = BigDecimal("0"), isValid = false),
        ),
        calculatedAt = "2021-08-09 14:46:48"
      )
    )
  }

  @Nested
  @DisplayName("get ROSH risk summary")
  inner class GetRoshRiskSummary {

    val crn = "DX12340A"

    @Test
    fun `returns registrations`() {
      val response = assessRiskAndNeedsApiRestClient.getRoshRiskSummary(crn)
      assertThat(response?.riskInCommunity?.high).isEqualTo(listOf("Public"))
      assertThat(response?.riskInCommunity?.medium).isEqualTo(listOf("Known adult", "Staff"))
      assertThat(response?.riskInCommunity?.low).isEqualTo(listOf("Children"))
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
