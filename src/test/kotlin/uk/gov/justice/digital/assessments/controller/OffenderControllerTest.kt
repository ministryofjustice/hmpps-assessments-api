package uk.gov.justice.digital.assessments.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.assessments.restclient.communityapi.GetOffenderDto
import uk.gov.justice.digital.assessments.testutils.IntegrationTest

class OffenderControllerTest: IntegrationTest() {

    val crn = "DX12340A"
    val oasysUserId = 101L
    @Test
    fun `access forbidden when no authority`() {
        webTestClient.get().uri("/offender/crn/$crn")
            .header("Content-Type", "application/json")
            .exchange()
            .expectStatus().isUnauthorized
    }

    @Test
    fun `get offender returns offender for crn`() {
        val offenderDto = webTestClient.get().uri("/offender/crn/$crn")
            .headers(setAuthorisation())
            .exchange()
            .expectStatus().isOk
            .expectBody<GetOffenderDto>()
            .returnResult()
            .responseBody

        assertThat(offenderDto?.offenderId).isEqualTo(oasysUserId)
    }

    @Test
    fun `get offender returns not found for invalid crn`() {
        val invalidCrn = "invalid"
        webTestClient.get().uri("/offender/crn/$invalidCrn")
            .headers(setAuthorisation())
            .exchange()
            .expectStatus().isNotFound
    }
}
