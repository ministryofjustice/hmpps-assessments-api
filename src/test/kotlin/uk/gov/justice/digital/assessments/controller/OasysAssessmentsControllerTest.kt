package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.EpisodeOasysAnswerDto
import uk.gov.justice.digital.assessments.api.OasysAssessmentEpisodeDto
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
@AutoConfigureWebTestClient(timeout = "600000")
class OasysAssessmentsControllerTest : IntegrationTest() {

  @Test
  fun `get latest closed episode for rsr oasys answers`() {
    val crn = "X1355"

    val latestClosedEpisode = webTestClient.get().uri("/subject/$crn/assessments/episodes/RSR/latest")
      .headers(setAuthorisation(roles = listOf("ROLE_ARN_READ_ONLY")))
      .exchange()
      .expectStatus().isOk
      .expectBody<OasysAssessmentEpisodeDto>()
      .returnResult()
      .responseBody

    assertThat(latestClosedEpisode).isNotNull
    assertThat(latestClosedEpisode.episodeUuid).isEqualTo(UUID.fromString("f7765470-efd5-4589-8fdd-4570360e5289"))
    assertThat(latestClosedEpisode.assessmentUuid).isEqualTo(
      UUID.fromString("49c8d211-68dc-4692-a6e2-d58468127356")
    )
    assertThat(latestClosedEpisode.created).isEqualTo(
      LocalDateTime.of(2019, 11, 14, 9, 0)
    )
    assertThat(latestClosedEpisode.ended).isEqualTo(
      LocalDateTime.of(2019, 11, 14, 13, 0)
    )
    assertThat(
      latestClosedEpisode.answers.episodeAnswers
    ).containsExactlyInAnyOrder(
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.1.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.33",
        answer = "11/12/2020",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.13.1_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.2.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.40",
        answer = "8",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "6.7",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.29",
        answer = "18/12/2020",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.10.1_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.34",
        answer = "5",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "12.1",
        answer = "1",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.35",
        answer = "3",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.7.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.9.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.8.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.12.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.13.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "9.2",
        answer = "2",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.10.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.39",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "9.1",
        answer = "2",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.30",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "11.2",
        answer = "1",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "R1.2.6.2_V2",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.38",
        answer = "01/11/2021",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.32",
        answer = "10",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "4.2",
        answer = "0",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.36",
        answer = "2",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.37",
        answer = "2",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "6.7.1",
        answer = "perpetrator",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.8.2",
        answer = "01/01/2020",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.42",
        answer = "YES",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "6.4",
        answer = "2",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "11.4",
        answer = "2",
      ),
      EpisodeOasysAnswerDto(
        questionCode = "1.41",
        answer = "YES",
      )
    )
  }
}
