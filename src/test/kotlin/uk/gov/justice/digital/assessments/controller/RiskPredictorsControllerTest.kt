package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.api.Score
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
@AutoConfigureWebTestClient(timeout = "360000")
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
  }
}