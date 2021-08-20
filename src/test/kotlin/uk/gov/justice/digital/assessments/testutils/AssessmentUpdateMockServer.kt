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

    // create assessment stubs
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 1, \"assessmentType\": \"SHORT_FORM_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 2, \"assessmentType\": \"SHORT_FORM_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(403)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentForbiddenJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 3, \"assessmentType\": \"SHORT_FORM_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(409)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentDuplicateJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(equalToJson("{ \"offenderPk\": 4, \"assessmentType\": \"SHORT_FORM_PSR\" }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    // update assessment stubs
    stubFor(
      WireMock.put(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(
          equalToJson(
            "{\n" +
              "    \"offenderPk\": 1,\n" +
              "    \"areaCode\": \"WWS\",\n" +
              "    \"oasysUserCode\": \"STUARTWHITLAM\",\n" +
              "    \"assessmentType\": \"SHORT_FORM_PSR\",\n" +
              "    \"oasysSetPk\": 1,\n" +
              "    \"answers\": [\n" +
              "        {\n" +
              "            \"sectionCode\": \"ROSH\",\n" +
              "            \"logicalPage\": 1,\n" +
              "            \"questionCode\": \"R1.3\",\n" +
              "            \"answer\": \"YES\",\n" +
              "            \"isStatic\": false\n" +
              "        }]\n" +
              "}",
            true,
            true
          )
        )
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(updateAssessmentJson)
        )
    )

    stubFor(
      WireMock.put(WireMock.urlEqualTo("/assessments"))
        .withRequestBody(
          equalToJson(
            "{\n" +
              "    \"offenderPk\": 1,\n" +
              "    \"areaCode\": \"WWS\",\n" +
              "    \"oasysUserCode\": \"STUARTWHITLAM\",\n" +
              "    \"assessmentType\": \"SHORT_FORM_PSR\",\n" +
              "    \"oasysSetPk\": 1,\n" +
              "    \"answers\": []\n" +
              "}",
            true,
            true
          )
        )
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(updateAssessmentJson)
        )
    )
    // complete assessment successfully
    stubFor(
      WireMock.put(WireMock.urlEqualTo("/assessments/complete"))
        .withRequestBody(equalToJson("{ \"oasysSetPk\": 1, \"offenderPk\": 1, \"areaCode\": \"WWS\", \"oasysUserCode\": \"STUARTWHITLAM\", \"assessmentType\": \"SHORT_FORM_PSR\", \"ignoreWarnings\": true }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(completeAssessmentJson)
        )
    )

    // complete assessment forbidden
    stubFor(
      WireMock.put(WireMock.urlEqualTo("/assessments/complete"))
        .withRequestBody(
          equalToJson("{ \"oasysSetPk\": 1, \"offenderPk\": 2, \"areaCode\": \"WWS\", \"oasysUserCode\": \"STUARTWHITLAM\", \"assessmentType\": \"SHORT_FORM_PSR\", \"ignoreWarnings\": true }", true, true)
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(403)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(createAssessmentForbiddenJson)
        )
    )

    // complete assessment returns oasys assessment validation error
    stubFor(
      WireMock.put(WireMock.urlEqualTo("/assessments/complete"))
        .withRequestBody(
          equalToJson("{ \"oasysSetPk\": 1, \"offenderPk\": 5, \"areaCode\": \"WWS\", \"oasysUserCode\": \"STUARTWHITLAM\", \"assessmentType\": \"SHORT_FORM_PSR\", \"ignoreWarnings\": true }", true, true)
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(completeAssessmentErrorJson)
        )
    )

    // complete assessment oasys server error
    stubFor(
      WireMock.put(WireMock.urlEqualTo("/assessments/complete"))
        .withRequestBody(equalToJson("{ \"oasysSetPk\": 1, \"offenderPk\": 4, \"areaCode\": \"WWS\", \"oasysUserCode\": \"STUARTWHITLAM\", \"assessmentType\": \"SHORT_FORM_PSR\", \"ignoreWarnings\": true }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
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

    val updateAssessmentJson =
      """{ "oasysSetPk": "1" }""".trimIndent()

    val completeAssessmentJson =
      """{ "oasysSetPk": "1" , "validationErrorDtos": [] }""".trimIndent()

    val completeAssessmentErrorJson =
      """{     "oasysSetPk": 1, "validationErrorDtos": [
        {
            "sectionCode": "ASSESSMENT",
            "questionCode": "R1.2",
            "errorType": "mandatory_question_missing",
            "message": "R1.2 Has offender been convicted of any of the following offences.",
            "assessmentValidationError": true
        }]}
      """.trimIndent()
  }
}
