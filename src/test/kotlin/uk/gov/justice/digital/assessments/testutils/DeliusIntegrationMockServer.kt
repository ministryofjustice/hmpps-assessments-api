package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class DeliusIntegrationMockServer : WireMockServer(9097) {

  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubGetOffenderPersonalCircumstances() {
    val crn = "DX5678A"
    val eventId = "1"
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data$crn/$eventId"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              ""
            )
        )
    )
  }

//
//   fun stubGetOffenderPersonalContacts() {
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/personalContacts"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(personalContact)
//         )
//     )
//   }
//
//   fun stubGetOffenderPersonalContactsPregnant() {
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:DX5678B|X1356|CRN1|X1355)/personalContacts"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(personalContact)
//         )
//     )
//   }
//
//   fun stubGetOffender() {
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340A/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(offenderDto("DX12340A"))
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340F/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(offenderDto("DX12340F"))
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1355/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(offenderDto("X1355"))
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1356|CRN1)/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(offenderDto("X1356"))
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(offenderDto("DX5678A"))
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678B/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(offenderDto("DX5678B"))
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(404)
//             .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidBadRequest/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(400)
//             .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidUnauthorized/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(401)
//             .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidForbidden/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(403)
//             .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotKnow/all"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(422)
//             .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}")
//         )
//     )
//   }
//
//   fun stubGetOffenderRegistrations() {
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340A/registrations"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(
//               mapToJson(
//                 CommunityRegistrations(
//                   listOf(
//                     CommunityRegistration(
//                       active = true,
//                       warnUser = true,
//                       riskColour = "Red",
//                       registerCategory = CommunityRegistrationElement("M2", "MAPPA Cat 2"),
//                       registerLevel = CommunityRegistrationElement("M1", "MAPPA Level 1"),
//                       type = CommunityRegistrationElement("MAPP", "MAPPA"),
//                       startDate = LocalDate.parse("2021-10-10"),
//                     ),
//                     CommunityRegistration(
//                       active = true,
//                       warnUser = true,
//                       riskColour = "Red",
//                       registerCategory = CommunityRegistrationElement("RC12", "Hate Crime - Disability"),
//                       type = CommunityRegistrationElement("IRMO", "Hate Crime"),
//                       startDate = LocalDate.parse("2021-10-10"),
//                     ),
//                     CommunityRegistration(
//                       active = true,
//                       warnUser = false,
//                       riskColour = "Red",
//                       type = CommunityRegistrationElement("RHRH", "High RoSH"),
//                       startDate = LocalDate.parse("2021-10-10"),
//                     ),
//                   )
//                 )
//               )
//             )
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/registrations"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(404)
//             .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidBadRequest/registrations"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(400)
//             .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidUnauthorized/registrations"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(401)
//             .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidForbidden/registrations"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(403)
//             .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}")
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotKnow/registrations"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(422)
//             .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}")
//         )
//     )
//   }
//
//   fun stubGetConvictions() {
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1|DX|CRN)[a-zA-Z0-9]*/convictions"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(convictionsJson)
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1|DX|CRN)[a-zA-Z0-9]*/convictions/123456"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(convictionJson)
//         )
//     )
//
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/convictions"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(404)
//         )
//     )
//   }
//
//   fun stubGetPrimaryIds() {
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/primaryIdentifiers?includeActiveOnly=true&page=0&size=100"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(primaryIdsJson)
//         )
//     )
//   }
//
//   fun stubGetUserAccess() {
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1|DX|CRN)[a-zA-Z0-9]*/user/([a-zA-Z0-9/-]*)/userAccess"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withBody(laoSuccess)
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(OX[a-zA-Z0-9]*)/user/([a-zA-Z0-9/-]*)/userAccess"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(403)
//             .withBody(laoFailure)
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/invalidNotFound/user/([a-zA-Z0-9/-]*)/userAccess"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(404)
//             .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidBadRequest/user/user1/userAccess"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(400)
//             .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidUnauthorized/user/user1/userAccess"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(401)
//             .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
//         )
//     )
//     stubFor(
//       WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotKnow/user/user1/userAccess"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(422)
//             .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}")
//         )
//     )
//   }
//
//   fun stubUploadDocument() {
//     stubFor(
//       WireMock.post(WireMock.urlEqualTo("/secure/offenders/crn/X1355/convictions/1/document"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(201)
//             .withBody(
//               """
//               {
//                 "id": 2500356804,
//                 "documentName": "file.txt",
//                 "crn": "X1355",
//                 "dateLastModified": "2021-11-11T17:51:22",
//                 "lastModifiedUser": "Name,User",
//                 "creationDate": "2021-11-11T17:51:22"
//               }
//               """.trimIndent()
//             )
//         )
//     )
//
//     mockDocumentUploadErrorResponse(400)
//     mockDocumentUploadErrorResponse(401)
//     mockDocumentUploadErrorResponse(403)
//     mockDocumentUploadErrorResponse(404)
//     mockDocumentUploadErrorResponse(500)
//     mockDocumentUploadErrorResponse(501)
//     mockDocumentUploadErrorResponse(502)
//     mockDocumentUploadErrorResponse(503)
//   }
//
//   private fun mockDocumentUploadErrorResponse(statusCode: Int) {
//     stubFor(
//       WireMock.post(WireMock.urlEqualTo("/secure/offenders/crn/X1$statusCode/convictions/2500000223/document"))
//         .willReturn(
//           WireMock.aResponse()
//             .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
//             .withStatus(statusCode)
//         )
//     )
//   }
//
//   private fun mapToJson(dto: Any): String {
//     return objectMapper.writeValueAsString(dto)
//   }
//
}
