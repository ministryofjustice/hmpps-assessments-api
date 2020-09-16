package uk.gov.justice.digital.assessments.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient

@AutoConfigureWebTestClient
class QuestionControllerTest : IntegrationTest() {

    private val questionSchemaId = 1234L

    @Test
    fun `access forbidden when no authority`() {
        webTestClient.get().uri("/questions/id/$questionSchemaId")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isUnauthorized
    }

}