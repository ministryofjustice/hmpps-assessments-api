package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class AssessmentUpdateMockServer : WireMockServer(9003) {
  fun stubCreateOffender() {

    // offender stubs
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340A\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createOffenderJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"X1356\", \"areaCode\" : \"WWS\", \"oasysUserCode\" : \"STUARTWHITLAM\", \"deliusEvent\" : 1}", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createOffenderJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"CRN1\", \"areaCode\" : \"WWS\", \"oasysUserCode\" : \"STUARTWHITLAM\", \"deliusEvent\" : 12345}", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createOffenderJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340B\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(403)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createOffenderDuplicateJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340C\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(409)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createOffenderDuplicateJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340D\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340E\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody("ERROR")
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340F\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offenders"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX5678A\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createOffenderJson)
        )
    )
  }

  fun stubOffenderStub() {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/offender/stub"))
        .withRequestBody(equalToJson("{ \"crn\": \"DX12340A\", \"pnc\": \"A/1234560BA\", \"familyName\": \"Smith\", \"forename1\": \"John\", \"areaCode\": \"WWS\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
        )
    )
  }

  companion object {
    val createOffenderJson =
      """{ "oasysOffenderId": "1" }""".trimIndent()
    val createOffenderDuplicateJson =
      """{ "developerMessage": "Duplicate offender found" }""".trimIndent()
  }
}
