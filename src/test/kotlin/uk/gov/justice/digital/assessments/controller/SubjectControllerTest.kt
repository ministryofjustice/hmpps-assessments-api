package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.services.dto.OasysAnswers
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDateTime
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
@AutoConfigureWebTestClient(timeout ="600000")
class SubjectControllerTest : IntegrationTest() {

  @Test
  fun `get latest closed episode rsr oasys answers`() {
    val crn = "X1346"

    val latestClosedEpisode = webTestClient.get().uri("/subject/$crn/assessments/episodes/RSR/current")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<OasysAssessmentEpisodeDto>()
      .returnResult()
      .responseBody

    Assertions.assertThat(latestClosedEpisode).isEqualTo(
      OasysAssessmentEpisodeDto(
        episodeId = 5,
        episodeUuid = UUID.fromString("f3569440-efd5-4289-8fdd-4560360e5299"),
        assessmentUuid = UUID.fromString("19c8d211-68dc-4692-a6e2-d58468127056"),
        created = LocalDateTime.of(2019, 11, 14, 9, 0),
        ended = LocalDateTime.of(2019, 11, 14, 13, 0),
        answers = OasysAnswers()
      )
    )
  }
}