package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.api.AssessmentDto
import uk.gov.justice.digital.assessments.api.CreateAssessmentDto
import java.time.LocalDateTime

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
        webTestClient.post().uri("/assessments/supervision")
                .bodyValue(CreateAssessmentDto("SupervisionId"))
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentDto>()
                .consumeWith {
                    val assessment = it.responseBody
                    assertThat(assessment?.assessmentId).isEqualTo(1)
                    assertThat(assessment?.supervisionId).isEqualTo("SupervisionId")
                    assertThat(assessment?.createdDate).isEqualToIgnoringMinutes(LocalDateTime.now())
                }
    }

    @Test
    fun `trying to create an assessment when one already exists returns the existing assessment`() {
        // create assessment
        val assessment = webTestClient.post().uri("/assessments/supervision")
                .bodyValue(CreateAssessmentDto("ExistingSupervisionId"))
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentDto>()
                .returnResult()
                .responseBody

        // try and create another
        val existing = webTestClient.post().uri("/assessments/supervision")
                .bodyValue(CreateAssessmentDto("ExistingSupervisionId"))
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<AssessmentDto>()
                .returnResult()
                .responseBody

        assertThat(existing.assessmentId).isEqualTo(assessment.assessmentId)
        assertThat(existing.supervisionId).isEqualTo(assessment.supervisionId)
        assertThat(existing.createdDate).isEqualTo(assessment.createdDate)
    }
}