package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class AssessmentUpdateMockServer : WireMockServer(9003) {
  fun stubCreateOffender() {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340A\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(assessmentUpdateJson)
        )
    )
  }

  companion object {
    val assessmentUpdateJson =
      """{ "oasysOffenderId": "1" }""".trimIndent()
  }
}
