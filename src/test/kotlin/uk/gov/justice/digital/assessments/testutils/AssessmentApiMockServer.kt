package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class AssessmentApiMockServer : WireMockServer(9004) {
  fun stubGetAssessment() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetId/1"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(assessmentJson),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetId/2"))
        .willReturn(
          WireMock.aResponse()
            .withBody(getAssessmentNotFoundJson)
            .withStatus(404)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json"))),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetId/3"))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json"))),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetId/4"))
        .willReturn(
          WireMock.aResponse()
            .withBody("ERROR")
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json"))),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetId/5"))
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json"))),
        ),
    )

    stubFor(
      WireMock.get(
        WireMock.urlEqualTo("/assessments/latest/DX5678A?status=SIGNED&status=COMPLETE&types=LAYER_1&types=LAYER_3&cloneable=false"),
      )
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(cloneableAssessment),
        ),
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 1 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(filteredReferenceDataJson),
        ),
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 2 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json"))),
        ),
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 3 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(referenceData400Error),
        ),
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 4 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(401)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(referenceData401Error),
        ),
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 5 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(404)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(referenceData404Error),
        ),
    )
  }

  companion object {

    val assessmentJson =

      """{
    "assessmentId": 1,
    "refAssessmentVersionCode": "LAYER3",
    "assessmentType": "LAYER_3",
    "assessmentStatus": "COMPLETE",
    "historicStatus": "CURRENT",
    "assessorName": "Probation Test",
    "created": "2016-03-16T10:16:38",
    "completed": "2018-06-20T23:00:09",
    "sections": [
        {
            "sectionId": 8078548,
            "refSectionCode": "3",
            "refSectionCrimNeedScoreThreshold": 2,
            "status": "COMPLETE_LOCKED",
            "sectionOgpWeightedScore": 0,
            "sectionOgpRawScore": 0,
            "sectionOvpWeightedScore": 0,
            "sectionOvpRawScore": 0,
            "sectionOtherWeightedScore": 0,
            "lowScoreAttentionNeeded": false,
            "questions": [
                {
                    "refQuestionId": 1,
                    "refQuestionCode": "3.3",
                    "oasysQuestionId": 1,
                    "displayOrder": 1,
                    "questionText": "Currently of no fixed abode or in transient accommodation",
                    "currentlyHidden": false,
                    "disclosed": false,
                    "answers": []
                },
                {
                    "refQuestionId": 2,
                    "refQuestionCode": "3.4",
                    "oasysQuestionId": 2,
                    "displayOrder": 2,
                    "questionText": "Suitability of accommodation",
                    "currentlyHidden": false,
                    "disclosed": false,
                    "answers": []
                }
            ]
        }
    ]
}
      """.trimIndent()

    val getAssessmentNotFoundJson =
      """{ "status": 404 , "developerMessage": "Assessment not found" }""".trimIndent()

    val referenceData400Error =
      """{ "status": 400 , "developerMessage": "Bad Request" }""".trimIndent()

    val referenceData401Error =
      """{ "status": 401 ,"developerMessage": "Not Authorised" }""".trimIndent()

    val referenceData404Error =
      """{ "status": 404 ,"developerMessage": "Not Found" }""".trimIndent()

    val filteredReferenceDataJson =
      """{
            "assessor_office": [
              {
                "longDescription": "Test Assessment Office 1",
                "description": "Test Assessment 1",
                "code": "123456"
              },
              {
                "longDescription": "Test Assessment Office 2",
                "description": "Test Assessment 2",
                "code": "123456"
              }
            ]
        }
      """.trimIndent()

    val offenderStubs = """
    [
    {
        "offenderPk": 8001,
        "crn": "D001022",
        "familyName": "John",
        "forename1": "David",
        "laoIndicator": "N",
        "areaCode": "WWS",
        "createDate": "2021-06-16T12:58:23",
        "createUser": "STUARTWHITLAM",
        "updatedDate": "2021-06-16T12:58:23",
        "updatedUser": "STUARTWHITLAM"
    },
    {
        "offenderPk": 3001,
        "crn": "D001040",
        "familyName": "Whitlam",
        "forename1": "Stuart",
        "areaCode": "WWS",
        "createDate": "2021-04-26T10:43:37",
        "createUser": "STUARTWHITLAM",
        "updatedDate": "2021-04-26T14:05:11",
        "updatedUser": "STUARTWHITLAM"
    },
    {
        "offenderPk": 9001,
        "crn": "X320741",
        "familyName": "Smith",
        "forename1": "John",
        "laoIndicator": "N",
        "areaCode": "WWS",
        "createDate": "2021-06-30T13:10:27",
        "createUser": "STUARTWHITLAM",
        "updatedDate": "2021-06-30T13:10:27",
        "updatedUser": "STUARTWHITLAM"
    }
   ]
      
    """.trimIndent()
  }

  val cloneableAssessment = this::class.java.getResource("/json/oasysLatestRSRFullAssessment.json")?.readText()
}
