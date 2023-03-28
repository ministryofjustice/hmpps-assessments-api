package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class CommunityApiMockServer : WireMockServer(9096) {

  fun stubGetUserAccess() {
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1|DX|CRN)[a-zA-Z0-9]*/user/([a-zA-Z0-9/-]*)/userAccess"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(laoSuccess)
        )
    )
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(OX[a-zA-Z0-9]*)/user/([a-zA-Z0-9/-]*)/userAccess"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(403)
            .withBody(laoFailure)
        )
    )
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/invalidNotFound/user/([a-zA-Z0-9/-]*)/userAccess"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidBadRequest/user/user1/userAccess"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidUnauthorized/user/user1/userAccess"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotKnow/user/user1/userAccess"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(422)
            .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}")
        )
    )
  }

  fun stubUploadDocument() {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/secure/offenders/crn/X1355/convictions/1/document"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(201)
            .withBody(
              """
              {
                "id": 2500356804,
                "documentName": "file.txt",
                "crn": "X1355",
                "dateLastModified": "2021-11-11T17:51:22",
                "lastModifiedUser": "Name,User",
                "creationDate": "2021-11-11T17:51:22"
              }
              """.trimIndent()
            )
        )
    )

    mockDocumentUploadErrorResponse(400)
    mockDocumentUploadErrorResponse(401)
    mockDocumentUploadErrorResponse(403)
    mockDocumentUploadErrorResponse(404)
    mockDocumentUploadErrorResponse(500)
    mockDocumentUploadErrorResponse(501)
    mockDocumentUploadErrorResponse(502)
    mockDocumentUploadErrorResponse(503)
  }

  private fun mockDocumentUploadErrorResponse(statusCode: Int) {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/secure/offenders/crn/X1$statusCode/convictions/2500000223/document"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(statusCode)
        )
    )
  }

  private val laoSuccess = """{
      "exclusionMessage": null,
      "restrictionMessage": null,
      "userExcluded": false,
      "userRestricted": false
    }
  """.trimIndent()

  private val laoFailure = """{
      "exclusionMessage": "excluded",
      "restrictionMessage": "restricted",
      "userExcluded": true,
      "userRestricted": true
    }
  """.trimIndent()
}
