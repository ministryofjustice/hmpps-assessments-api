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
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 1, \"assessmentType\": \"SHORT_FORMAT_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 2, \"assessmentType\": \"SHORT_FORMAT_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(403)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentForbiddenJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 3, \"assessmentType\": \"SHORT_FORMAT_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(409)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentDuplicateJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 4, \"assessmentType\": \"SHORT_FORMAT_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )
  }

  companion object {
    val createOffenderJson =
      """{ "oasysOffenderId": "1" }""".trimIndent()

    val createOffenderForbiddenJson =
      """{ "developerMessage": "User not authorised" }""".trimIndent()

    val createOffenderDuplicateJson =
      """{ "developerMessage": "Duplicate offender found" }""".trimIndent()

    val createAssessmentForbiddenJson =
      """{ "developerMessage": "User not authorised" }""".trimIndent()

    val createAssessmentDuplicateJson =
      """{ "developerMessage": "Existing assessment found" }""".trimIndent()

    val createAssessmentJson =
      """{ "oasysSetPk": "1" }""".trimIndent()
  }
}
