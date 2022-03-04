package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.api.Score
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ScoreLevel
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.dto.ScoreType
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
class RiskPredictorsControllerTest : IntegrationTest() {

  @Test
  fun `get predictors draft returns predictor`() {
    val episodeUuid = UUID.fromString("163cf020-ff53-4dc6-a15c-e93e8537d347")
    val final = false
    assessRisksAndNeedsApiMockServer.stubGetRSRPredictorsForOffenderAndOffences(final, episodeUuid)

    val predictors = webTestClient.get().uri("/risks/predictors/episodes/$episodeUuid?final=$final")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<List<PredictorScoresDto>>()
      .returnResult()
      .responseBody

    Assertions.assertThat(predictors).isEqualTo(
      listOf(
        PredictorScoresDto(
          type = PredictorType.RSR,
          scoreType = ScoreType.STATIC,
          scores = mapOf(
            "RSR" to Score(
              level = ScoreLevel.HIGH.name,
              score = BigDecimal("11.34"),
              isValid = true,
              date = "2021-08-09 14:46:48"
            ),
            "OSPC" to Score(
              level = ScoreLevel.NOT_APPLICABLE.name,
              score = BigDecimal("0"),
              isValid = false,
              date = "2021-08-09 14:46:48"
            ),
            "OSPI" to Score(
              level = ScoreLevel.NOT_APPLICABLE.name,
              score = BigDecimal("0"),
              isValid = false,
              date = "2021-08-09 14:46:48"
            ),
          )
        )
      )
    )
  }

  @Test
  fun `should return forbidden when user does not have LAO permissions for offender`() {

    val episodeUuid = UUID.fromString("3df6172f-a931-4fb9-a595-46868893b4ed")
    val final = false
    assessRisksAndNeedsApiMockServer.stubGetRSRPredictorsForOffenderAndOffences(final, episodeUuid)

    webTestClient.get().uri("/risks/predictors/episodes/$episodeUuid?final=$final")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isForbidden
      .expectBody<ErrorResponse>()
      .consumeWith {
        Assertions.assertThat(it.responseBody?.status).isEqualTo(403)
        Assertions.assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
      }
  }
}
