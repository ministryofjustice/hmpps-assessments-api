package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.api.PredictorScoresDto
import uk.gov.justice.digital.assessments.api.Score
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.PredictorSubType
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.ScoreLevel
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import uk.gov.justice.digital.assessments.services.dto.ScoreType
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.testutils.Verify
import uk.gov.justice.digital.assessments.utils.RequestData
import java.math.BigDecimal
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
@AutoConfigureWebTestClient
class AssessmentControllerTest : IntegrationTest() {
  val assessmentUuid = "2e020e78-a81c-407f-bc78-e5f284e237e5"
  val episodeId = "current"

  val laoFailureAssessmentUuid = "6e60784e-584e-4762-952d-d7288e31d4f4"
  val laoFailureEpisodeUuid = "3df6172f-a931-4fb9-a595-46868893b4ed"

  @Nested
  @DisplayName("fetching subject")
  inner class FetchingSubject {
    @Test
    fun `get the subject details for an assessment`() {
      val subject = fetchAssessmentSubject("19c8d211-68dc-4692-a6e2-d58468127056")

      assertThat(subject.name).isEqualTo("John Smith")
      assertThat(subject.dob).isEqualTo("2001-01-01")
      assertThat(subject.age).isEqualTo(20)
      assertThat(subject.crn).isEqualTo("X1346")
      assertThat(subject.pnc).isEqualTo("dummy-pnc")
    }

    @Test
    fun `get the subject details for an assessment returns forbidden when user does not have LAO permission`() {
      webTestClient.get().uri("/assessments/$laoFailureAssessmentUuid/subject")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isForbidden
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(403)
          assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
        }
    }
  }

  @Nested
  @DisplayName("fetching assessment episodes")
  inner class FetchingEpisodes {
    @Test
    fun `fetch all episodes for an assessment`() {
      val episodes = fetchEpisodes(assessmentUuid)
      assertThat(episodes).hasSize(2)
    }

    @Test
    fun `fetch current episode for an assessment`() {
      val episode = fetchCurrentEpisode(assessmentUuid)

      assertThat(episode.assessmentUuid).isEqualTo(UUID.fromString(assessmentUuid))
      assertThat(episode.created).isEqualToIgnoringSeconds(LocalDateTime.of(2019, 11, 14, 9, 0))
      assertThat(episode.ended).isNull()
      assertThat(episode.answers).isEmpty()
    }

    @Test
    fun `get episodes returns not found when assessment does not exist`() {
      val invalidAssessmentId = UUID.randomUUID()
      webTestClient.get().uri("/assessments/$invalidAssessmentId/episodes")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `get current episode returns not found when assessment does not exist`() {
      val invalidAssessmentId = UUID.randomUUID()
      webTestClient.get().uri("/assessments/$invalidAssessmentId/episodes/current")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `get episodes returns forbidden when user does not have LAO permission`() {

      webTestClient.get().uri("/assessments/$laoFailureAssessmentUuid/episodes")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isForbidden
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(403)
          assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
        }
    }

    @Test
    fun `get current episode returns forbidden when user does not have LAO permission`() {

      webTestClient.get().uri("/assessments/$laoFailureAssessmentUuid/episodes/current")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isForbidden
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(403)
          assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
        }
    }
  }

  @Nested
  @DisplayName("updating episode")
  inner class UpdatingEpisode {
    @Test
    fun `updates episode answers`() {
      val newQuestionCode = "question_code"
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionCode to listOf("new free text"))
      )
      val episode =
        webTestClient.post().uri("/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259")
          .bodyValue(updateEpisodeDto)
          .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
          .exchange()
          .expectStatus().isOk
          .expectBody<AssessmentEpisodeDto>()
          .returnResult()
          .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.answers).containsKey(newQuestionCode)

      Verify.singleAnswer(episode?.answers?.get(newQuestionCode)!!, "new free text")
    }

    @Test
    fun `updates episode answers returns forbidden when user does not have LAO permission`() {
      val newQuestionCode = "question_code"
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionCode to listOf("new free text"))
      )
      val episode =
        webTestClient.post().uri("/assessments/$laoFailureAssessmentUuid/episodes/$laoFailureEpisodeUuid")
          .bodyValue(updateEpisodeDto)
          .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
          .exchange()
          .expectStatus().isForbidden
          .expectBody<ErrorResponse>()
          .consumeWith {
            assertThat(it.responseBody?.status).isEqualTo(403)
            assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
          }
    }

    @Test
    fun `update episode answers from JSON`() {
      val newQuestionCode = "new_q_code"
      val answerText = "one day I'll fly away"
      val jsonString = "{\"answers\":{\"${newQuestionCode}\":[\"${answerText}\"]}}"

      updateEpisodeFromJson(newQuestionCode, answerText, jsonString)
    }

    @Test
    fun `update episode answers from JSON - single answer, not array`() {
      val questionCode = "q_code"
      val answerText = "one day I'll fly away"
      val jsonString = "{\"answers\":{\"${questionCode}\":\"${answerText}\"}}"

      updateEpisodeFromJson(questionCode, answerText, jsonString)
    }

    @Test
    fun `add episode table row from JSON`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestionCode = "question_code_for_test"
      val answerText = "child answer"
      val jsonString = "{\"answers\":{\"${childQuestionCode}\":\"${answerText}\"}}"

      val episode = addTableRowFromJson(tableName, jsonString)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(1)
      assertThat(rows.first()).isEqualTo(
        mapOf(childQuestionCode to listOf(answerText))
      )
    }

    @Test
    fun `add episode table row from JSON, single value`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val answerText = "child answer"
      val jsonString = "{\"answers\":{\"${childQuestion}\": \"${answerText}\"}}"

      val episode = addTableRowFromJson(tableName, jsonString)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(1)
      assertThat(rows.first()).isEqualTo(
        mapOf(childQuestion to listOf(answerText))
      )
    }

    @Test
    fun `add episode table row with multi-value from JSON`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val jsonString = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val episode = addTableRowFromJson(tableName, jsonString)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(1)
      assertThat(rows.first()).isEqualTo(
        mapOf(childQuestion to listOf(firstAnswer, secondAnswer))
      )
    }

    @Test
    fun `add several table rows from JSON`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val thirdAnswer = "answer3"

      val row1 = "{\"answers\":{\"${childQuestion}\":\"${firstAnswer}\"}}"
      val row2 = "{\"answers\":{\"${childQuestion}\":\"${secondAnswer}\"}}"
      val row3 = "{\"answers\":{\"${childQuestion}\":\"${thirdAnswer}\"}}"

      addTableRowFromJson(tableName, row1)
      addTableRowFromJson(tableName, row2)
      val episode = addTableRowFromJson(tableName, row3)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(3)

      assertThat(rows[0][childQuestion]).isEqualTo(listOf(firstAnswer))
      assertThat(rows[1][childQuestion]).isEqualTo(listOf(secondAnswer))
      assertThat(rows[2][childQuestion]).isEqualTo(listOf(thirdAnswer))
    }

    @Test
    fun `update episode table row from JSON`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val secondUpdate = "answer2-updated"
      val thirdAnswer = "answer3"

      val row1 = "{\"answers\":{\"${childQuestion}\":\"${firstAnswer}\"}}"
      val row2 = "{\"answers\":{\"${childQuestion}\":\"${secondAnswer}\"}}"
      val row3 = "{\"answers\":{\"${childQuestion}\":\"${thirdAnswer}\"}}"
      val row2Update = "{\"answers\":{\"${childQuestion}\":\"${secondUpdate}\"}}"

      addTableRowFromJson(tableName, row1)
      addTableRowFromJson(tableName, row2)
      addTableRowFromJson(tableName, row3)

      val episode = updateTableRowFromJson(tableName, 1, row2Update)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(3)

      assertThat(rows[0][childQuestion]).isEqualTo(listOf(firstAnswer))
      assertThat(rows[1][childQuestion]).isEqualTo(listOf(secondUpdate))
      assertThat(rows[2][childQuestion]).isEqualTo(listOf(thirdAnswer))
    }

    @Test
    fun `remove first of three table rows`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val thirdAnswer = "answer3"

      val row1 = "{\"answers\":{\"${childQuestion}\":\"${firstAnswer}\"}}"
      val row2 = "{\"answers\":{\"${childQuestion}\":\"${secondAnswer}\"}}"
      val row3 = "{\"answers\":{\"${childQuestion}\":\"${thirdAnswer}\"}}"

      addTableRowFromJson(tableName, row1)
      addTableRowFromJson(tableName, row2)
      addTableRowFromJson(tableName, row3)

      val episode = deleteTableRow(0)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(2)

      assertThat(rows[0][childQuestion]).isEqualTo(listOf(secondAnswer))
      assertThat(rows[1][childQuestion]).isEqualTo(listOf(thirdAnswer))
    }

    @Test
    fun `remove second of three table rows`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val thirdAnswer = "answer3"

      val row1 = "{\"answers\":{\"${childQuestion}\":\"${firstAnswer}\"}}"
      val row2 = "{\"answers\":{\"${childQuestion}\":\"${secondAnswer}\"}}"
      val row3 = "{\"answers\":{\"${childQuestion}\":\"${thirdAnswer}\"}}"

      addTableRowFromJson(tableName, row1)
      addTableRowFromJson(tableName, row2)
      addTableRowFromJson(tableName, row3)

      val episode = deleteTableRow(1)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(2)

      assertThat(rows[0][childQuestion]).isEqualTo(listOf(firstAnswer))
      assertThat(rows[1][childQuestion]).isEqualTo(listOf(thirdAnswer))
    }

    @Test
    fun `remove all three table rows`() {
      val tableName = "children_at_risk_of_serious_harm_test"
      val childQuestion = "question_code_for_test"
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val thirdAnswer = "answer3"

      val row1 = "{\"answers\":{\"${childQuestion}\":\"${firstAnswer}\"}}"
      val row2 = "{\"answers\":{\"${childQuestion}\":\"${secondAnswer}\"}}"
      val row3 = "{\"answers\":{\"${childQuestion}\":\"${thirdAnswer}\"}}"

      addTableRowFromJson(tableName, row1)
      addTableRowFromJson(tableName, row2)
      addTableRowFromJson(tableName, row3)

      deleteTableRow(2)
      deleteTableRow(1)
      val episode = deleteTableRow(0)

      val rows = episode.tables[tableName]!!
      assertThat(rows.size).isEqualTo(0)
    }

    private fun updateEpisodeFromJson(questionCode: String, expectedAnswer: String, jsonString: String) {
      val episode = updateFromJson(
        "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259",
        questionCode,
        jsonString
      )

      Verify.singleAnswer(episode.answers[questionCode]!!, expectedAnswer)
    }

    private fun addTableRowFromJson(tableName: String, jsonString: String): AssessmentEpisodeDto {
      val endpoint = "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259/table/children_at_risk_of_serious_harm_test"

      val episode = webTestClient.post().uri(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonString)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.tables).containsKey(tableName)

      return episode!!
    }

    private fun updateTableRowFromJson(tableName: String, index: Int, jsonString: String): AssessmentEpisodeDto {
      val endpoint =
        "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259/table/children_at_risk_of_serious_harm_test/$index"

      val episode = webTestClient.put().uri(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonString)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.tables).containsKey(tableName)

      return episode!!
    }

    private fun deleteTableRow(index: Int): AssessmentEpisodeDto {
      val endpoint =
        "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259/table/children_at_risk_of_serious_harm_test/$index"
      val episode = webTestClient.delete().uri(endpoint)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull

      return episode!!
    }

    private fun updateFromJson(
      endpoint: String,
      questionCode: String,
      jsonString: String
    ): AssessmentEpisodeDto {
      val episode = webTestClient.post().uri(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonString)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.answers).containsKey(questionCode)

      return episode!!
    }

    @Test
    fun `does not update episode answers if episode is closed`() {
      val newQuestionCode = "new_question_code"
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionCode to listOf("new free text"))
      )
      webTestClient.post().uri("/assessments/$assessmentUuid/episodes/d7aafe55-0cff-4f20-a57a-b66d79eb9c91")
        .bodyValue(updateEpisodeDto)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }
  }

  @Nested
  @DisplayName("completing assessment episodes")
  inner class CompletingEpisodes {
    private val assessmentUuid = UUID.fromString("e399ed1b-0e77-4c68-8bbc-d2f0befece84")
    private val episodeUuid = UUID.fromString("163cf020-ff53-4dc6-a15c-e93e8537d347")

    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/assessments/$assessmentUuid/complete")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `complete assessment episode returns episode`() {
      assessRisksAndNeedsApiMockServer.stubGetRSRPredictorsForOffenderAndOffences(true, episodeUuid)

      val assessmentEpisode = webTestClient.post().uri("/assessments/$assessmentUuid/complete")
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode?.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(assessmentEpisode?.ended).isEqualToIgnoringMinutes(LocalDateTime.now())
      assertThat(assessmentEpisode?.predictors).isEqualTo(
        listOf(
          PredictorScoresDto(
            type = PredictorType.RSR,
            scoreType = ScoreType.STATIC,
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

    @Test
    fun `complete episode returns not found when there are no current episodes for assessment`() {
      val noEpisodesUUID = UUID.fromString("6082265e-885d-4526-b713-77e59b70691e")
      webTestClient.post().uri("/assessments/$noEpisodesUUID/complete")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isNotFound
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    @Test
    fun `complete assessment episode with errors returns assessment level errors`() {
      val assessmentErrorsUUID = UUID.fromString("aa47e6c4-e41f-467c-95e7-fcf5ffd422f5")
      val assessmentEpisode = webTestClient.post().uri("/assessments/$assessmentErrorsUUID/complete")
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode?.ended).isNull()
      assertThat(assessmentEpisode?.assessmentErrors).hasSize(1)
    }

    @Test
    fun `should return bad request when no user area header is set when completing assessment`() {
      val roshAssessmentUuid = UUID.fromString("aa47e6c4-e41f-467c-95e7-fcf5ffd422f5")
      webTestClient.post().uri("/assessments/$roshAssessmentUuid/complete")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(400)
          assertThat(it.responseBody?.developerMessage).isEqualTo("Area Code Header is mandatory")
        }
    }

    @Test
    fun `should return forbidden when user does not have LAO permission for offender`() {
      val roshAssessmentUuid = UUID.fromString("aa47e6c4-e41f-467c-95e7-fcf5ffd422f5")
      webTestClient.post().uri("/assessments/$laoFailureAssessmentUuid/complete")
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isForbidden
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(403)
          assertThat(it.responseBody?.reason).isEqualTo("LAO_PERMISSION")
        }
    }
  }

  private fun fetchAssessmentSubject(assessmentUuid: String): AssessmentSubjectDto {
    val subject = webTestClient.get().uri("/assessments/$assessmentUuid/subject")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentSubjectDto>()
      .returnResult()
      .responseBody
    return subject!!
  }

  private fun fetchEpisodes(assessmentUuid: String): List<AssessmentEpisodeDto> {
    return webTestClient.get().uri("/assessments/$assessmentUuid/episodes")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<List<AssessmentEpisodeDto>>()
      .returnResult()
      .responseBody!!
  }

  private fun fetchCurrentEpisode(assessmentUuid: String): AssessmentEpisodeDto {
    return fetchEpisode(assessmentUuid, episodeId)
  }

  private fun fetchEpisode(assessmentUuid: String, episodeId: String): AssessmentEpisodeDto {
    return webTestClient.get().uri("/assessments/$assessmentUuid/episodes/$episodeId")
      .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentEpisodeDto>()
      .returnResult()
      .responseBody!!
  }
}
