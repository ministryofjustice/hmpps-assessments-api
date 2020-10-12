package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.*
import java.time.LocalDateTime
import java.util.*

@SqlGroup(
        Sql(scripts = ["classpath:assessments/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:assessments/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@AutoConfigureWebTestClient(timeout = "50000")
class AssessmentControllerTest : IntegrationTest() {
    private val supervisionId = "SUPERVISION1"

    @Test
    fun `access forbidden when no authority`() {
        webTestClient.get().uri("/assessments/supervision/$supervisionId")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `create a new assessment returns assessment`() {
        val assessment = createAssessment("SupervisionId")

        assertThat(assessment.supervisionId).isEqualTo("SupervisionId")
        assertThat(assessment.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())
    }

    @Test
    fun `trying to create an assessment when one already exists returns the existing assessment`() {
        val existingAssessment = createAssessment("ExistingSupervisionId")
        val assessmentDto = createAssessment("ExistingSupervisionId")

        assertThat(assessmentDto.assessmentId).isEqualTo(existingAssessment.assessmentId)
        assertThat(assessmentDto.assessmentUuid).isEqualTo(existingAssessment.assessmentUuid)
        assertThat(assessmentDto.supervisionId).isEqualTo(existingAssessment.supervisionId)
        assertThat(assessmentDto.createdDate).isEqualTo(existingAssessment.createdDate)
    }

    @Test
    fun `creates new episode on existing assessment`() {

        val episode = webTestClient.post().uri("/assessments/f9a07b3f-91b7-45a7-a5ca-2d98cf1147d8/episodes")
                .bodyValue(CreateAssessmentEpisodeDto("Change of Circs"))
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
    fun `retrieves episodes for an assessment`() {
        val episodes = webTestClient.get().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<List<AssessmentEpisodeDto>>()
                .returnResult()
                .responseBody

        assertThat(episodes).hasSize(2)
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
    fun `retrieves current episode for an assessment`() {

        val episode = webTestClient.get().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes/current")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentEpisodeDto>()
                .returnResult()
                .responseBody

        assertThat(episode?.assessmentUuid).isEqualTo(UUID.fromString("2e020e78-a81c-407f-bc78-e5f284e237e5"))
        assertThat(episode?.created).isEqualToIgnoringSeconds(LocalDateTime.of(2019,11,14,9,0))
        assertThat(episode?.ended).isNull()
        assertThat(episode?.answers).isEmpty()
    }

    @Test
    fun `get current episode returns not found when assessment does not exist`() {
        val invalidAssessmentId = UUID.randomUUID()
        webTestClient.get().uri("/assessments/$invalidAssessmentId/episodes/current")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    fun `updates episode answers`() {
        val newQuestionUUID = UUID.randomUUID()
        val newAnswerUUID = UUID.randomUUID()
        val updateEpisodeDto = UpdateAssessmentEpisodeDto(mapOf(newQuestionUUID to AnswerDto(freeTextAnswer = "new free text", answers = mapOf(newAnswerUUID to "answer 2"))))
        val episode = webTestClient.post().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes/f3569440-efd5-4289-8fdd-4560360e5259")
                .bodyValue(updateEpisodeDto)
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentEpisodeDto>()
                .returnResult()
                .responseBody

        assertThat(episode?.answers).containsKey(newQuestionUUID)

        val answer = episode?.answers?.get(newQuestionUUID)
        assertThat(answer?.answers).hasSize(1)
        assertThat(answer?.answers).containsKey(newAnswerUUID)
    }

    @Test
    fun `does not update episode answers if episode is closed`() {
        val newQuestionUUID = UUID.randomUUID()
        val newAnswerUUID = UUID.randomUUID()
        val updateEpisodeDto = UpdateAssessmentEpisodeDto(mapOf(newQuestionUUID to AnswerDto(freeTextAnswer = "new free text", answers = mapOf(newAnswerUUID to "answer 2"))))
        webTestClient.post().uri("/assessments/2e020e78-a81c-407f-bc78-e5f284e237e5/episodes/d7aafe55-0cff-4f20-a57a-b66d79eb9c91")
                .bodyValue(updateEpisodeDto)
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<ErrorResponse>()
                .returnResult()
                .responseBody
    }

    private fun createAssessment(supervisionId: String): AssessmentDto {
        val assessment = webTestClient.post().uri("/assessments/supervision")
                .bodyValue(CreateAssessmentDto(supervisionId))
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentDto>()
                .returnResult()
                .responseBody
        return assessment!!
    }
}