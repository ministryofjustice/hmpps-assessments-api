package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class AuditMockServer : WireMockServer(9008) {

  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubAuditEvents() {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/audit")).atPriority(1)
        .withRequestBody(WireMock.equalToJson("{\"who\": \"error-user@justice.gov.uk\"}", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(500)
            .withBody(auditError),
        ),
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/audit")).atPriority(5)
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(201),
        ),
    )
  }

  private val auditError = """{
      "status": 500,
      "errorCode": 0,
      "userMessage": "string",
      "developerMessage": "string",
      "moreInfo": "string"
  }
  """.trimIndent()
}
