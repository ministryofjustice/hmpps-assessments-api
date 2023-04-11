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
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.api.assessments.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.assessments.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import uk.gov.justice.digital.assessments.testutils.Verify
import java.time.LocalDateTime
import java.util.UUID

@SqlGroup(
  Sql(
    scripts = ["classpath:assessments/before-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
  ),
  Sql(
    scripts = ["classpath:assessments/after-test.sql"],
    config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED),
    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
  ),
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
    fun `should return current episode for a given crn`() {
      // given
      val crn = "X1346"
      val path = "/assessments/subject/$crn/episodes/current"

      // when
      val assessmentEpisode = webTestClient.get().uri(path)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      // then
      assertThat(assessmentEpisode.episodeUuid).isEqualTo(UUID.fromString("f3569440-efd5-4289-8fdd-4560360e5279"))
    }

    @Test
    fun `should return 404 not found for a non existent crn`() {
      // given
      val crn = "This does not exist"
      val path = "/assessments/subject/$crn/episodes/current"

      // when & then
      webTestClient.get().uri(path)
        .headers(setAuthorisation(roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isNotFound
    }

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
        mapOf(newQuestionCode to listOf("new free text")),
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

      Verify.singleAnswer(episode?.answers?.get(newQuestionCode)!! as List<String>, "new free text")
    }

    @Test
    fun `updates episode answers returns forbidden when user does not have LAO permission`() {
      val newQuestionCode = "question_code"
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(
        mapOf(newQuestionCode to listOf("new free text")),
      )
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

    private fun updateEpisodeFromJson(questionCode: String, expectedAnswer: String, jsonString: String) {
      val episode = updateFromJson(
        "/assessments/$assessmentUuid/episodes/f3569440-efd5-4289-8fdd-4560360e5259",
        questionCode,
        jsonString,
        "USERNAME",
      )

      Verify.singleAnswer(episode.answers[questionCode]!! as List<String>, expectedAnswer)
      assertThat(episode.userFullName).isEqualTo("USERNAME")
    }

    private fun updateFromJson(
      endpoint: String,
      questionCode: String,
      jsonString: String,
      user: String,
    ): AssessmentEpisodeDto {
      val episode = webTestClient.post().uri(endpoint)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(jsonString)
        .headers(setAuthorisation(fullName = user, roles = listOf("ROLE_PROBATION")))
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
        mapOf(newQuestionCode to listOf("new free text")),
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
    fun `should return episode when completing current episode using assessment UUID`() {
      completeAndAssertEpisode("/assessments/$assessmentUuid/complete", LocalDateTime.now())
    }

    @Test
    fun `should return episode when completing current episode using episode UUID`() {
      completeAndAssertEpisode("/assessments/$assessmentUuid/episodes/$episodeUuid/complete", LocalDateTime.now())
    }

    @Test
    fun `should return episode when episode is already complete using episode UUID`() {
      val completedEpisodeUuid = "df43079f-c263-4690-b77e-f8689aa4a093"
      completeAndAssertEpisode(
        "/assessments/$assessmentUuid/episodes/$completedEpisodeUuid/complete",
        LocalDateTime.of(2021, 1, 2, 0, 0),
      )
    }

    private fun completeAndAssertEpisode(endpointUrl: String, episodeEndDate: LocalDateTime) {
      val assessmentEpisode = webTestClient.post().uri(endpointUrl)
        .headers(setAuthorisation(fullName = "NEW USER", roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode?.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(assessmentEpisode?.ended).isEqualToIgnoringMinutes(episodeEndDate)
      assertThat(assessmentEpisode?.userFullName).isEqualTo("NEW USER")
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
    fun `should return forbidden when user does not have LAO permission for offender`() {
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

  @Nested
  @DisplayName("Closing assessment episodes")
  inner class ClosingEpisodes {
    private val assessmentUuid = UUID.fromString("e399ed1b-0e77-4c68-8bbc-d2f0befece84")
    private val episodeUuid = UUID.fromString("163cf020-ff53-4dc6-a15c-e93e8537d347")

    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/assessments/$assessmentUuid/episodes/$episodeUuid/close")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `close assessment episode returns episode`() {
      val assessmentEpisode = webTestClient.get().uri("/assessments/$assessmentUuid/episodes/$episodeUuid/close")
        .headers(setAuthorisation(fullName = "NEW USER", roles = listOf("ROLE_PROBATION")))
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode?.assessmentUuid).isEqualTo(assessmentUuid)
      assertThat(assessmentEpisode?.closedDate).isEqualToIgnoringMinutes(LocalDateTime.now())
      assertThat(assessmentEpisode?.userFullName).isEqualTo("NEW USER")
    }

    @Test
    fun `should return forbidden when user does not have LAO permission for offender`() {
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
