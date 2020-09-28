package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlConfig
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.AssessmentEpisodeDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentEpisodeDto
import java.time.LocalDateTime

@SqlGroup(
        Sql(scripts = ["classpath:assessments/before-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED)),
        Sql(scripts = ["classpath:assessments/after-test.sql"], config = SqlConfig(transactionMode = SqlConfig.TransactionMode.ISOLATED), executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
)
@AutoConfigureWebTestClient
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
        // create assessment
        val assessment = createAssessment("ExistingSupervisionId")

        // try and create another
        val existing = createAssessment("ExistingSupervisionId")

        assertThat(existing.assessmentId).isEqualTo(assessment.assessmentId)
        assertThat(existing.supervisionId).isEqualTo(assessment.supervisionId)
        assertThat(existing.createdDate).isEqualTo(assessment.createdDate)
    }

    @Test
    fun `creates new episode on existing assessment`() {

        val episode = webTestClient.post().uri("/assessments/2/episodes")
                .bodyValue(CreateAssessmentEpisodeDto("Change of Circs"))
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentEpisodeDto>()
                .returnResult()
                .responseBody


        assertThat(episode?.assessmentId).isEqualTo(2)
        assertThat(episode?.created).isEqualToIgnoringMinutes(LocalDateTime.now())
        assertThat(episode?.answers).isEmpty()
    }

    @Test
    fun `retrieves episodes for an assessment`() {
        val episodes = webTestClient.get().uri("/assessments/1/episodes")
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

            webTestClient.get().uri("/assessments/9999/episodes")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isNotFound

    }

    @Test
    fun `retrieves current episode for an assessment`() {

        val episode = webTestClient.get().uri("/assessments/1/episodes/current")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentEpisodeDto>()
                .returnResult()
                .responseBody

        assertThat(episode?.assessmentId).isEqualTo(1)
        assertThat(episode?.created).isEqualToIgnoringSeconds(LocalDateTime.of(2019,11,14,9,0))
        assertThat(episode?.ended).isNull()
        assertThat(episode?.answers).isEmpty()
    }

    @Test
    fun `get current episode returns not found when assessment does not exist`() {

        webTestClient.get().uri("/assessments/9999/episodes/current")
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isNotFound

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