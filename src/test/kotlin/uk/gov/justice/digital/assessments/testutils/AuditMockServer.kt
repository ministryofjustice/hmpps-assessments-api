package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class AuditMockServer : WireMockServer(9008) {
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
