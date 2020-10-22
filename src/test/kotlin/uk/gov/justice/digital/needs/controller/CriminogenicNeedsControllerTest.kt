package uk.gov.justice.digital.needs.controller

import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient

import org.springframework.test.web.reactive.server.expectBody
import uk.gov.justice.digital.needs.api.CalculateNeedsDto
import uk.gov.justice.digital.needs.api.CriminogenicNeedsDto

@AutoConfigureWebTestClient
class CriminogenicNeedsControllerTest: IntegrationTest() {

    @Test
    fun `calculate Criminogenic Needs from questions and answers`() {
        val calculateNeedsDto = CalculateNeedsDto()
        val supervisionId = 1L
        val criminogenicNeeds = webTestClient.post().uri("/needs/$supervisionId")
                .bodyValue(calculateNeedsDto)
                .headers(setAuthorisation())
                .exchange()
                .expectStatus().isOk
                .expectBody<CriminogenicNeedsDto>()
                .returnResult()
                .responseBody
    }
}