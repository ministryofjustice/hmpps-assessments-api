package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.api.FilteredReferenceDataRequest
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RefElementDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.util.*

@SqlGroup(
  Sql(scripts = ["classpath:filteredReferenceData/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:filteredReferenceData/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@AutoConfigureWebTestClient(timeout = "50000")
class ReferenceDataControllerTest : IntegrationTest() {
  private val validAssessmentUuid = UUID.fromString("2e020e78-a81c-407f-bc78-e5f284e237e5")
  private val validQuestionUuid = UUID.fromString("ead7aa8a-e20d-4822-9b3b-aedf186333b1")
  private val validParentQuestionUuid = UUID.fromString("a64dc082-980a-43e0-b443-cf196f35d571")
  private val invalidUuid = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")

  @Nested
  @DisplayName("Fetching filtered reference data")
  inner class FetchingFilteredReferenceData {

    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/referencedata/filtered")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `returns filtered reference data`() {
      val currentEpisode = fetchCurrentEpisode(validAssessmentUuid.toString())
      val response = webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          validAssessmentUuid,
          currentEpisode.episodeUuid!!,
          validQuestionUuid,
          mapOf(validParentQuestionUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<Map<String, List<RefElementDto>>>()
        .returnResult()
        .responseBody

      assertThat(response?.get("assessor_office")?.first()).isInstanceOf(RefElementDto::class.java)
    }

    @Test
    fun `returns not found when unable to find OASys mapping for the question`() {
      val currentEpisode = fetchCurrentEpisode(validAssessmentUuid.toString())
      webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          validAssessmentUuid,
          currentEpisode.episodeUuid!!,
          invalidUuid,
          mapOf(validParentQuestionUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isNotFound
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    @Test
    fun `returns not found when unable to find OASys mapping for parent questions`() {
      val currentEpisode = fetchCurrentEpisode(validAssessmentUuid.toString())
      webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          validAssessmentUuid,
          currentEpisode.episodeUuid!!,
          validQuestionUuid,
          mapOf(invalidUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isNotFound
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    @Test
    fun `returns not found when reference data client receives a 404`() {
      val assessmentUuid = UUID.fromString("8177b6c7-1b20-459b-b6ee-0aeeb2f16857")
      val currentEpisode = fetchCurrentEpisode(assessmentUuid.toString())
      webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          assessmentUuid,
          currentEpisode.episodeUuid!!,
          validQuestionUuid,
          mapOf(validParentQuestionUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isNotFound
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    @Test
    fun `returns not authorised when reference data client receives a 401`() {
      val assessmentUuid = UUID.fromString("80fd9a2a-59dd-4783-8cac-1689a0464437")
      val currentEpisode = fetchCurrentEpisode(assessmentUuid.toString())
      webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          assessmentUuid,
          currentEpisode.episodeUuid!!,
          validQuestionUuid,
          mapOf(validParentQuestionUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isUnauthorized
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    @Test
    fun `returns bad request when reference data client receives a 400`() {
      val assessmentUuid = UUID.fromString("bd5e5a88-c0ac-4f55-9c08-b8e8bdd9568c")
      val currentEpisode = fetchCurrentEpisode(assessmentUuid.toString())
      webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          assessmentUuid,
          currentEpisode.episodeUuid!!,
          validQuestionUuid,
          mapOf(validParentQuestionUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isBadRequest
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    @Test
    fun `returns server error when reference data client receives a 500`() {
      val assessmentUuid = UUID.fromString("bbbae903-7803-4206-800c-2d3b81116d5c")
      val currentEpisode = fetchCurrentEpisode(assessmentUuid.toString())
      webTestClient.post().uri("/referenceData/filtered")
        .bodyValue(FilteredReferenceDataRequest(
          assessmentUuid,
          currentEpisode.episodeUuid!!,
          validQuestionUuid,
          mapOf(validParentQuestionUuid to "test"),
        ))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().is5xxServerError
        .expectBody<ErrorResponse>()
        .returnResult()
        .responseBody
    }

    private fun fetchCurrentEpisode(assessmentGuid: String): AssessmentEpisodeDto {
      return webTestClient.get().uri("/assessments/$assessmentGuid/episodes/current")
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody!!
    }
  }
}
