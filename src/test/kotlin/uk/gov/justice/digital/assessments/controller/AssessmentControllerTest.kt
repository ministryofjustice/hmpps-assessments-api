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
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.AssessmentSubjectDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.ErrorResponse
import uk.gov.justice.digital.assessments.api.UpdateAssessmentEpisodeDto
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.LocalDateTime
import java.util.UUID

@SqlGroup(
  Sql(scripts = ["classpath:assessments/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
  Sql(scripts = ["classpath:assessments/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@AutoConfigureWebTestClient(timeout = "50000")
class AssessmentControllerTest : IntegrationTest() {
  private val supervisionId = "SUPERVISION1"

  @Nested
  @DisplayName("creating assessments")
  inner class CreatingAssessment {
    @Test
    fun `access forbidden when no authority`() {
      webTestClient.get().uri("/assessments/supervision/$supervisionId")
        .header("Content-Type", "application/json")
        .exchange()
        .expectStatus().isUnauthorized
    }

    @Test
    fun `creating a new assessment returns assessment`() {
      val assessment = createAssessment("SupervisionId")

      assertThat(assessment.supervisionId).isEqualTo("SupervisionId")
      assertThat(assessment.assessmentId).isNotNull()
      assertThat(assessment.assessmentUuid).isNotNull()
      assertThat(assessment.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())
    }

    @Test
    fun `create a new assessment from court details, creates subject and episode, returns assessment`() {
      val assessment = createAssessment("SHF06", "668911253")

      assertThat(assessment.supervisionId).isNull()
      assertThat(assessment.assessmentId).isNotNull()
      assertThat(assessment.assessmentUuid).isNotNull()
      assertThat(assessment.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())

      val subject = fetchAssessmentSubject(assessment.assessmentUuid!!)
      assertThat(subject.assessmentUuid).isEqualTo(assessment.assessmentUuid)
      assertThat(subject.name).isEqualTo("John Smith")
      assertThat(subject.dob).isEqualTo("1979-08-18")
      assertThat(subject.crn).isEqualTo("DX12340A")
      assertThat(subject.pnc).isEqualTo("A/1234560BA")
      assertThat(subject.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())

      val episodes = fetchEpisodes(assessment.assessmentUuid!!.toString())
      assertThat(episodes).hasSize(1)
      assertThat(episodes[0].oasysAssessmentId).isEqualTo(1)
    }

    @Test
    fun `creates new episode on existing assessment`() {
      val episode = webTestClient.post().uri("/assessments/f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8/episodes")
        .bodyValue(CreateAssessmentEpisodeDto("Change of Circs", AssessmentType.SHORT_FORM_PSR))
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode?.assessmentUuid).isEqualTo(UUID.fromString("f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8"))
      assertThat(episode?.created).isEqualToIgnoringMinutes(LocalDateTime.now())
      assertThat(episode?.answers).isEmpty()
    }

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

    @Test
    fun `creating an assessment for a supervision when one already exists returns the existing assessment`() {
      val existingAssessment = createAssessment("ExistingSupervisionId")
      val assessmentDto = createAssessment("ExistingSupervisionId")

      assertThat(assessmentDto.assessmentId).isEqualTo(existingAssessment.assessmentId)
      assertThat(assessmentDto.assessmentUuid).isEqualTo(existingAssessment.assessmentUuid)
      assertThat(assessmentDto.supervisionId).isEqualTo(existingAssessment.supervisionId)
      assertThat(assessmentDto.createdDate).isEqualTo(existingAssessment.createdDate)
    }

    @Test
    fun `creating an assessment from court details when one already exists returns existing assessment`() {
      val assessment = createAssessment("courtCode", "caseNumber")

      assertThat(assessment.supervisionId).isNull()
      assertThat(assessment.assessmentId).isEqualTo(2)
      assertThat(assessment.assessmentUuid).isEqualTo(UUID.fromString("19c8d211-68dc-4692-a6e2-d58468127056"))
    }
  }

  @Nested
  @DisplayName("fetching assessment episodes")
  inner class FetchingEpisodes {
    @Test
    fun `fetch all episodes for an assessment`() {
      val episodes = fetchEpisodes("2e020e78-a81c-407f-bc78-e5f284e237e5")
      assertThat(episodes).hasSize(2)
    }

    @Test
    fun `fetch current episode for an assessment`() {
      val episode = fetchCurrentEpisode("2e020e78-a81c-407f-bc78-e5f284e237e5")

      assertThat(episode.assessmentUuid).isEqualTo(UUID.fromString("2e020e78-a81c-407f-bc78-e5f284e237e5"))
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
      val episode = webTestClient.post().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes/f3569440-efd5-4289-8fdd-4560360e5259")
        .bodyValue(updateEpisodeDto)
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody

      assertThat(episode).isNotNull
      assertThat(episode?.answers).containsKey(newQuestionUUID)

      val answer = episode?.answers?.get(newQuestionUUID)!!
      assertThat(answer.size).isEqualTo(1)
      assertThat(answer.first()).isEqualTo("new free text")
    }

    @Test
    fun `update episode answers from JSON`() {
      val newQuestionUUID = UUID.randomUUID()
      val answerText = "one day I'll fly away"
      val jsonString = "{\"answers\":{\"${newQuestionUUID}\":[\"${answerText}\"]}}"

      updateFromJSON(newQuestionUUID, answerText, jsonString)
    }

    @Test
    fun `update episode answers from JSON - single answer, not array`() {
      val newQuestionUUID = UUID.randomUUID()
      val answerText = "one day I'll fly away"
      val jsonString = "{\"answers\":{\"${newQuestionUUID}\":\"${answerText}\"}}"

      updateFromJSON(newQuestionUUID, answerText, jsonString)
    }

    fun updateFromJSON(questionUUID: UUID, expectedAnswer: String, jsonString: String) {
      val episode = webTestClient.post().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes/f3569440-efd5-4289-8fdd-4560360e5259")
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

      val answer = episode?.answers?.get(questionUUID)!!
      assertThat(answer.size).isEqualTo(1)
      assertThat(answer.first()).isEqualTo(expectedAnswer)
    }

    @Test
    fun `does not update episode answers if episode is closed`() {
      val newQuestionUUID = UUID.randomUUID()
      val updateEpisodeDto = UpdateAssessmentEpisodeDto(mapOf(newQuestionUUID to listOf("new free text")))
      webTestClient.post().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes/d7aafe55-0cff-4f20-a57a-b66d79eb9c91")
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
        .headers(setAuthorisation())
        .exchange()
        .expectStatus().isOk
        .expectBody<AssessmentEpisodeDto>()
        .returnResult()
        .responseBody
      assertThat(assessmentEpisode.ended).isNull()
      assertThat(assessmentEpisode.assessmentErrors).hasSize(1)

    }
  }

  private fun createAssessment(supervisionId: String): AssessmentDto {
    return createAssessment(CreateAssessmentDto(supervisionId, assessmentType = AssessmentType.SHORT_FORM_PSR))
  }

  private fun createAssessment(courtCode: String, caseNumber: String): AssessmentDto {
    return createAssessment(CreateAssessmentDto(courtCode = courtCode, caseNumber = caseNumber, assessmentType = AssessmentType.SHORT_FORM_PSR))
  }

  private fun createAssessment(cad: CreateAssessmentDto): AssessmentDto {
    val assessment = webTestClient.post().uri("/assessments/supervision")
      .bodyValue(cad)
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentDto>()
      .returnResult()
      .responseBody
    return assessment!!
  }

  private fun fetchAssessmentSubject(assessmentGuid: UUID): AssessmentSubjectDto {
    return fetchAssessmentSubject(assessmentGuid.toString())
  }

  private fun fetchAssessmentSubject(assessmentGuid: String): AssessmentSubjectDto {
    val subject = webTestClient.get().uri("/assessments/$assessmentGuid/subject")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentSubjectDto>()
      .returnResult()
      .responseBody
    return subject!!
  }

  private fun fetchEpisodes(assessmentGuid: String): List<AssessmentEpisodeDto> {
    return webTestClient.get().uri("/assessments/$assessmentGuid/episodes")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<List<AssessmentEpisodeDto>>()
      .returnResult()
      .responseBody!!
  }

  private fun fetchCurrentEpisode(assessmentGuid: String): AssessmentEpisodeDto {
    return fetchEpisode(assessmentGuid, "current")
  }

  private fun fetchEpisode(assessmentGuid: String, episodeGuid: String): AssessmentEpisodeDto {
    return webTestClient.get().uri("/assessments/$assessmentGuid/episodes/$episodeGuid")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isOk
      .expectBody<AssessmentEpisodeDto>()
      .returnResult()
      .responseBody!!
  }
}
