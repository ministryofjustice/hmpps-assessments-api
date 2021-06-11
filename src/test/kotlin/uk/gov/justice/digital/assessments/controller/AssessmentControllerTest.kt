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
import uk.gov.justice.digital.assessments.api.*
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.utils.RequestData
import uk.gov.justice.digital.assessments.testutils.Verify
import java.time.LocalDateTime
import java.util.UUID

@SqlGroup(
  Sql(scripts = ["classpath:assessments/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:assessments/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@AutoConfigureWebTestClient(timeout = "50000")
class AssessmentControllerTest : IntegrationTest() {
  val assessmentUuid = "2e020e78-a81c-407f-bc78-e5f284e237e5"
  val episodeId = "current"

  @Nested
  @DisplayName("fetching subject")
  inner class FetchingSubject {
    @Test
    fun `get the subject details for an assessment`() {
      val subject = fetchAssessmentSubject("19c8d211-68dc-4692-a6e2-d58468127056")

      assertThat(subject.assessmentUuid).isEqualTo(UUID.fromString("19c8d211-68dc-4692-a6e2-d58468127056"))
      assertThat(subject.name).isEqualTo("John Smith")
      assertThat(subject.dob).isEqualTo("1928-08-01")
      assertThat(subject.age).isGreaterThanOrEqualTo(92)
      assertThat(subject.crn).isEqualTo("dummy-crn")
      assertThat(subject.pnc).isEqualTo("dummy-pnc")
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
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isNotFound
    }

    @Test
    fun `get current episode returns not found when assessment does not exist`() {
      val invalidAssessmentId = UUID.randomUUID()
      webTestClient.get().uri("/assessments/$invalidAssessmentId/episodes/current")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isNotFound
    }
  }

  @Nested
  @DisplayName("updating episode")
  inner class UpdatingEpisode {
    @Test
    fun `updates episode answers`() {
      val newQuestionUUID = UUID.randomUUID()
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionUUID to listOf("new free text"))
      )
      val episode = webTestClient.post().uri("/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259")
        .bodyValue(updateEpisodeDto)
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.answers).containsKey(newQuestionUUID)

      Verify.singleAnswer(episode?.answers?.get(newQuestionUUID)!!, "new free text")
    }

    @Test
    fun `update episode answers from JSON`() {
      val newQuestionUUID = UUID.randomUUID()
      val answerText = "one day I'll fly away"
      val jsonString = "{\"answers\":{\"${newQuestionUUID}\":[\"${answerText}\"]}}"

      updateEpisodeFromJson(newQuestionUUID, answerText, jsonString)
    }

    @Test
    fun `update episode answers from JSON - single answer, not array`() {
      val newQuestionUUID = UUID.randomUUID()
      val answerText = "one day I'll fly away"
      val jsonString = "{\"answers\":{\"${newQuestionUUID}\":\"${answerText}\"}}"

      updateEpisodeFromJson(newQuestionUUID, answerText, jsonString)
    }

    @Test
    fun `add episode table row from JSON`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val answerText = "child answer"
      val jsonString = "{\"answers\":{\"${childQuestion}\":\"${answerText}\"}}"

      val episode = addTableRowFromJson(childQuestion, jsonString)

      Verify.singleAnswer(episode.answers.get(childQuestion)!!, answerText)
    }

    @Test
    fun `add episode table row from JSON, single value array`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val answerText = "child answer"
      val jsonString = "{\"answers\":{\"${childQuestion}\":[\"${answerText}\"]}}"

      val episode = addTableRowFromJson(childQuestion, jsonString)

      assertThat(episode).isNotNull
      assertThat(episode.answers).containsKey(childQuestion)

      Verify.singleAnswer(episode.answers.get(childQuestion)!!, answerText)
    }

    @Test
    fun `add episode table row with multivalue from JSON`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val firstAnswer = "answer1"
      val secondAnswer = "answer2"
      val jsonString = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val episode = addTableRowFromJson(childQuestion, jsonString)

      // This looks wrong, but is down to an asymmetry in the way AnswersDto
      // serialises to Json and back. In practice, we only deserialise in tests,
      // so this shouldn't be problem
      val answer = episode.answers.get(childQuestion)!!.answers
      assertThat(answer.size).isEqualTo(2)
      assertThat(answer.first().items).isEqualTo(listOf(firstAnswer))
      assertThat(answer.last().items).isEqualTo(listOf(secondAnswer))
    }

    @Test
    fun `add several table rows from JSON`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val firstAnswer = "row1-answer1"
      val secondAnswer = "row1-answer2"
      val row1 = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val thirdAnswer = "row2-answer1"
      val forthAnswer = "row2-answer2"
      val row2 = "{\"answers\":{\"${childQuestion}\":[\"${thirdAnswer}\",\"${forthAnswer}\"]}}"

      val fifthAnswer = "row3-answer1"
      val sixthAnswer = "row3-answer2"
      val row3 = "{\"answers\":{\"${childQuestion}\":[\"${fifthAnswer}\",\"${sixthAnswer}\"]}}"

      addTableRowFromJson(childQuestion, row1)
      addTableRowFromJson(childQuestion, row2)
      val episode = addTableRowFromJson(childQuestion, row3)

      val answers = episode.answers.get(childQuestion)!!.answers.toList()
      assertThat(answers.size).isEqualTo(3)

      val row1Values = answers[0].items
      assertThat(row1Values).hasSize(2)
      assertThat(row1Values.first()).isEqualTo(firstAnswer)
      assertThat(row1Values.last()).isEqualTo(secondAnswer)

      val row2Values = answers[1].items
      assertThat(row2Values).hasSize(2)
      assertThat(row2Values.first()).isEqualTo(thirdAnswer)
      assertThat(row2Values.last()).isEqualTo(forthAnswer)

      val row3Values = answers[2].items
      assertThat(row3Values).hasSize(2)
      assertThat(row3Values.first()).isEqualTo(fifthAnswer)
      assertThat(row3Values.last()).isEqualTo(sixthAnswer)
    }


    @Test
    fun `update episode table row from JSON`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val firstAnswer = "row1-answer1"
      val secondAnswer = "row1-answer2"
      val row1 = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val thirdAnswer = "row2-answer1"
      val forthAnswer = "row2-answer2"
      val row2 = "{\"answers\":{\"${childQuestion}\":[\"${thirdAnswer}\",\"${forthAnswer}\"]}}"

      val fifthAnswer = "row3-answer1"
      val sixthAnswer = "row3-answer2"
      val row3 = "{\"answers\":{\"${childQuestion}\":[\"${fifthAnswer}\",\"${sixthAnswer}\"]}}"

      val thirdUpdate= "row2-updated"
      val row2Update = "{\"answers\":{\"${childQuestion}\":\"${thirdUpdate}\"}}"

      addTableRowFromJson(childQuestion, row1)
      addTableRowFromJson(childQuestion, row2)
      addTableRowFromJson(childQuestion, row3)

      val episode = updateTableRowFromJson(childQuestion, 1, row2Update)

      val answers = episode.answers.get(childQuestion)!!.answers.toList()
      assertThat(answers.size).isEqualTo(3)

      val row1Values = answers[0].items
      assertThat(row1Values).hasSize(2)
      assertThat(row1Values.first()).isEqualTo(firstAnswer)
      assertThat(row1Values.last()).isEqualTo(secondAnswer)

      val row2Values = answers[1].items
      assertThat(row2Values).hasSize(1)
      assertThat(row2Values.first()).isEqualTo(thirdUpdate)

      val row3Values = answers[2].items
      assertThat(row3Values).hasSize(2)
      assertThat(row3Values.first()).isEqualTo(fifthAnswer)
      assertThat(row3Values.last()).isEqualTo(sixthAnswer)
    }

    @Test
    fun `remove first of three table rows`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val firstAnswer = "row1-answer1"
      val secondAnswer = "row1-answer2"
      val row1 = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val thirdAnswer = "row2-answer1"
      val forthAnswer = "row2-answer2"
      val row2 = "{\"answers\":{\"${childQuestion}\":[\"${thirdAnswer}\",\"${forthAnswer}\"]}}"

      val fifthAnswer = "row3-answer1"
      val sixthAnswer = "row3-answer2"
      val row3 = "{\"answers\":{\"${childQuestion}\":[\"${fifthAnswer}\",\"${sixthAnswer}\"]}}"

      addTableRowFromJson(childQuestion, row1)
      addTableRowFromJson(childQuestion, row2)
      addTableRowFromJson(childQuestion, row3)

      val episode = deleteTableRow(0)

      val answers = episode.answers.get(childQuestion)!!.answers.toList()
      assertThat(answers.size).isEqualTo(2)

      val row2Values = answers[0].items
      assertThat(row2Values).hasSize(2)
      assertThat(row2Values.first()).isEqualTo(thirdAnswer)
      assertThat(row2Values.last()).isEqualTo(forthAnswer)

      val row3Values = answers[1].items
      assertThat(row3Values).hasSize(2)
      assertThat(row3Values.first()).isEqualTo(fifthAnswer)
      assertThat(row3Values.last()).isEqualTo(sixthAnswer)
    }

    @Test
    fun `remove second of three table rows`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val firstAnswer = "row1-answer1"
      val secondAnswer = "row1-answer2"
      val row1 = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val thirdAnswer = "row2-answer1"
      val forthAnswer = "row2-answer2"
      val row2 = "{\"answers\":{\"${childQuestion}\":[\"${thirdAnswer}\",\"${forthAnswer}\"]}}"

      val fifthAnswer = "row3-answer1"
      val sixthAnswer = "row3-answer2"
      val row3 = "{\"answers\":{\"${childQuestion}\":[\"${fifthAnswer}\",\"${sixthAnswer}\"]}}"

      addTableRowFromJson(childQuestion, row1)
      addTableRowFromJson(childQuestion, row2)
      addTableRowFromJson(childQuestion, row3)

      val episode = deleteTableRow(1)

      val answers = episode.answers.get(childQuestion)!!.answers.toList()
      assertThat(answers.size).isEqualTo(2)

      val row1Values = answers[0].items
      assertThat(row1Values).hasSize(2)
      assertThat(row1Values.first()).isEqualTo(firstAnswer)
      assertThat(row1Values.last()).isEqualTo(secondAnswer)

      val row3Values = answers[1].items
      assertThat(row3Values).hasSize(2)
      assertThat(row3Values.first()).isEqualTo(fifthAnswer)
      assertThat(row3Values.last()).isEqualTo(sixthAnswer)
    }

    @Test
    fun `remove all three table rows`() {
      val childQuestion = UUID.fromString("23c3e984-54c7-480f-b06c-7d000e2fb87c")
      val firstAnswer = "row1-answer1"
      val secondAnswer = "row1-answer2"
      val row1 = "{\"answers\":{\"${childQuestion}\":[\"${firstAnswer}\",\"${secondAnswer}\"]}}"

      val thirdAnswer = "row2-answer1"
      val forthAnswer = "row2-answer2"
      val row2 = "{\"answers\":{\"${childQuestion}\":[\"${thirdAnswer}\",\"${forthAnswer}\"]}}"

      val fifthAnswer = "row3-answer1"
      val sixthAnswer = "row3-answer2"
      val row3 = "{\"answers\":{\"${childQuestion}\":[\"${fifthAnswer}\",\"${sixthAnswer}\"]}}"

      addTableRowFromJson(childQuestion, row1)
      addTableRowFromJson(childQuestion, row2)
      addTableRowFromJson(childQuestion, row3)

      deleteTableRow(2)
      deleteTableRow(1)
      val episode = deleteTableRow(0)

      val answers = episode.answers.get(childQuestion)!!.answers.toList()
      assertThat(answers.size).isEqualTo(0)
    }

    private fun updateEpisodeFromJson(questionUUID: UUID, expectedAnswer: String, jsonString: String) {
      val episode = updateFromJson(
        "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259",
        questionUUID,
        jsonString)

      Verify.singleAnswer(episode.answers.get(questionUUID)!!, expectedAnswer)
    }

    private fun addTableRowFromJson(questionUUID: UUID, jsonString: String) : AssessmentEpisodeDto {
      return updateFromJson(
        "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259/children_at_risk_of_serious_harm",
        questionUUID,
        jsonString)
    }

    private fun updateTableRowFromJson(questionUUID: UUID, index: Int, jsonString: String) : AssessmentEpisodeDto {
      val endpoint = "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259/children_at_risk_of_serious_harm/$index"
      return updateFromJson(
        endpoint,
        questionUUID,
        jsonString)
    }

    private fun deleteTableRow(index: Int) : AssessmentEpisodeDto {
      val endpoint = "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259/children_at_risk_of_serious_harm/$index"
      val episode = webTestClient.delete().uri(endpoint)
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull

      return episode
    }

    private fun updateFromJson(
      endpoint: String,
      questionUUID: UUID,
      jsonString: String): AssessmentEpisodeDto
    {
      val episode = webTestClient.post().uri(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonString)
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.answers).containsKey(questionUUID)

      return episode
    }

    @Test
    fun `does not update episode answers if episode is closed`() {
      val newQuestionUUID = UUID.randomUUID()
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionUUID to listOf("new free text"))
      )
      webTestClient.post().uri("/assessments/$assessmentUuid/episodes/d7aafe55-0cff-4f20-a57a-b66d79eb9c91")
        .bodyValue(updateEpisodeDto)
        .headers(setAuthorisation())
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

    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/assessments/$assessmentUuid/complete")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `complete assessment episode returns episode`() {
      val assessmentEpisode = webTestClient.post().uri("/assessments/$assessmentUuid/complete")
        .header(RequestData.USER_AREA_HEADER_NAME, "WWS")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(assessmentEpisode.ended).isEqualToIgnoringMinutes(LocalDateTime.now())
    }

    @Test
    fun `complete episode returns not found when there are no current episodes for assessment`() {
      val noEpisodesUUID = UUID.fromString("6082265e-885d-4526-b713-77e59b70691e")
      webTestClient.post().uri("/assessments/$noEpisodesUUID/complete")
        .headers(setAuthorisation())
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
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode.ended).isNull()
      assertThat(assessmentEpisode.assessmentErrors).hasSize(1)
    }

    @Test
    fun `should return bad request when no user area header is set when completing assessment`() {
      webTestClient.post().uri("/assessments/$assessmentUuid/complete")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .consumeWith {
          assertThat(it.responseBody?.status).isEqualTo(400)
          assertThat(it.responseBody?.developerMessage).isEqualTo("Area Code Header is mandatory")
        }
    }
  }

  private fun fetchAssessmentSubject(assessmentUuid: String): AssessmentSubjectDto {
    val subject = webTestClient.get().uri("/assessments/$assessmentUuid/subject")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentSubjectDto>()
      .returnResult()
      .responseBody
    return subject!!
  }

  private fun fetchEpisodes(assessmentUuid: String): List<AssessmentEpisodeDto> {
    return webTestClient.get().uri("/assessments/$assessmentUuid/episodes")
      .headers(setAuthorisation())
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
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentEpisodeDto>()
      .returnResult()
      .responseBody!!
  }
}
