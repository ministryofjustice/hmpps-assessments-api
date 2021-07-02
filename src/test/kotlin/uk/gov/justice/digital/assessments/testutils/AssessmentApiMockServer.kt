package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import uk.gov.justice.digital.assessments.restclient.assessmentapi.RoleNames
import uk.gov.justice.digital.assessments.restclient.assessmentapi.Roles

class AssessmentApiMockServer : WireMockServer(9004) {
  fun stubGetAssessment() {

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetPk/1"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(assessmentJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetPk/2"))
        .willReturn(
          WireMock.aResponse()
            .withBody(getAssessmentNotFoundJson)
            .withStatus(404)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetPk/3"))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetPk/4"))
        .willReturn(
          WireMock.aResponse()
            .withBody("ERROR")
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/assessments/oasysSetPk/5"))
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 1 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(filteredReferenceDataJson)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 2 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(500)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 3 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(referenceData400Error)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 4 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(401)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(referenceData401Error)
        )
    )

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/referencedata/filtered"))
        .withRequestBody(WireMock.equalToJson("{ \"oasysSetPk\": 5 }", true, true))
        .willReturn(
          WireMock.aResponse()
            .withStatus(404)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(referenceData404Error)
        )
    )

    stubRBACPermissions(offenderPk = 1, permission = Roles.OFF_ASSESSMENT_CREATE.name)
    stubRBACPermissions(offenderPk = 1, oasysSetPk = 1, permission = Roles.ASSESSMENT_READ.name)
    stubRBACPermissions(offenderPk = 1, oasysSetPk = 2, permission = Roles.ASSESSMENT_READ.name)
    stubRBACPermissions(permission = Roles.RBAC_OTHER.name, roleName = RoleNames.CREATE_OFFENDER.name)
    stubRBACPermissions(offenderPk = 1, oasysSetPk = 1, permission = Roles.ASSESSMENT_EDIT.name)
    stubRBACPermissions(offenderPk = 5, oasysSetPk = 1, permission = Roles.ASSESSMENT_EDIT.name)
    stubRBACPermissions(offenderPk = 2, oasysSetPk = 1, permission = Roles.ASSESSMENT_EDIT.name)
    stubRBACPermissions(offenderPk = 2, permission = Roles.OFF_ASSESSMENT_CREATE.name)
    stubRBACPermissions(3, permission = Roles.OFF_ASSESSMENT_CREATE.name)
    stubRBACUnauthorisedPermissions(offenderPk = 7276800, permission = Roles.OFF_ASSESSMENT_CREATE.name)
    stubRBACUnauthorisedPermissions(offenderPk = 7276800, oasysSetPk = 1, permission = Roles.ASSESSMENT_EDIT.name)
    stubRBACBadRequestPermissionsForCreateOffenderAssessment()
  }

  private fun stubRBACBadRequestPermissionsForCreateOffenderAssessment() {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/authorisation/permissions"))
        .withRequestBody(
          WireMock.equalToJson(
            "{\"userCode\": \"STUARTWHITLAM\", \"roleChecks\" : [\"OFF_ASSESSMENT_CREATE\"],\"area\":\"WWS\", \"offenderPk\": null, \"oasysSetPk\" : null, \"assessmentType\": \"SHORT_FORM_PSR\", \"roleNames\" : [ ]}}",
            true,
            true
          )
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(400)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              "{" +
                "    \"status\": 400," +
                "    \"developerMessage\": \"Role checks [OFF_ASSESSMENT_CREATE], require parameter offenderPk\"" +
                "  }"
            )
        )
    )
  }

  private fun stubRBACPermissions(
    offenderPk: Long? = null,
    oasysSetPk: Long? = null,
    permission: String,
    roleName: String? = null
  ) {
    val roleNameString = if (roleName != null) "\"$roleName\"" else ""
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/authorisation/permissions"))
        .withRequestBody(
          WireMock.equalToJson(
            "{\"userCode\": \"STUARTWHITLAM\", \"roleChecks\" : [\"$permission\"],\"area\":\"WWS\", \"offenderPk\": $offenderPk, \"oasysSetPk\" : $oasysSetPk, \"assessmentType\": \"SHORT_FORM_PSR\", \"roleNames\" : [ $roleNameString ]}}",
            true,
            true
          )
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              "" +
                "{" +
                "    \"userCode\": \"STUARTWHITLAM\"," +
                "    \"offenderPk\": $offenderPk," +
                "    \"permissions\": [" +
                "        {" +
                "            \"checkCode\": \"$permission\"," +
                "            \"authorised\": true" +
                "        }" +
                "    ]" +
                "}"
            )
        )
    )
  }

  fun stubRBACUnauthorisedPermissions(offenderPk: Long? = null, oasysSetPk: Long? = null, permission: String, roleName: String? = null) {
    val roleNameString = if (roleName != null) "\"$roleName\"" else ""

    stubFor(
      WireMock.post(WireMock.urlEqualTo("/authorisation/permissions"))
        .withRequestBody(
          WireMock.equalToJson(
            "{\"userCode\": \"STUARTWHITLAM\", \"roleChecks\" : [\"$permission\"],\"area\":\"WWS\", \"offenderPk\": $offenderPk, \"oasysSetPk\" : $oasysSetPk, \"assessmentType\": \"SHORT_FORM_PSR\", \"roleNames\" : [ $roleNameString ]}}",
            true,
            true
          )
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(403)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              "{" +
                "    \"status\": 403," +
                "    \"developerMessage\": \"One of the permissions is Unauthorized\"," +
                "    \"payload\": {" +
                "        \"userCode\": \"STUARTWHITLAM\"," +
                "        \"offenderPk\": $offenderPk," +
                "        \"permissions\": [" +
                "            {" +
                "                \"checkCode\": \"$permission\"," +
                "                \"authorised\": false," +
                "                \"returnMessage\": \"STUART WHITLAM in Warwickshire is currently doing an assessment on this offender, created on 12/04/2021.\"" +
                "            }" +
                "        ]" +
                "    }" +
                "}"
            )
        )
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
  }
}
