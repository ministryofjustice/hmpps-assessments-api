package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class DeliusIntegrationMockServer : WireMockServer(9097) {

  fun stubGetCaseData() {
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/case-data/(?:DX5678A|X1356|CRN1|X1355)/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              crnWithPersonalContactsAndCircumstances()
            )
        )
    )

    stubFor(
      WireMock.get(WireMock.urlPathMatching("/case-data/(?:DX5678A|X1356|CRN1|X1355)/1"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              crnWithPersonalContactsAndCircumstances()
            )
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/DX5678B/1"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              crnWithPersonalContactsAndCircumstances()
            )
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidNotFound/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidBadRequest/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidUnauthorized/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidForbidden/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(403)
            .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidNotKnow/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(422)
            .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}")
        )
    )
  }

  private fun crnWithPersonalContactsAndCircumstances(): String {

    return """
{
    "crn": "DX5678A",
    "pncNumber": "A/1234560BA",
    "name": {
        "forename": "John",
        "surname": "Smith"
    },
    "dateOfBirth": "1979-08-18",
    "gender": "Male",
    "genderIdentity": "Non-Binary",
    "aliases": [
        {
            "name": {
                "forename": "John",
                "middleName": "",
                "surname": "Smithy"
            },
            "dateOfBirth": "1979-09-18"
        },
        {
            "name": {
                "forename": "Jonny",
                "middleName": "",
                "surname": "Smith"
            },
            "dateOfBirth": "1979-08-17"
        }
    ],
    "emailAddress": "address1@gmail.com",
    "phoneNumbers": [
        {
            "type": "MOBILE",
            "number": "071838893"
        },
        {
            "type": "TELEPHONE",
            "number": "0123456999"
        }
    ],
    "mainAddress": {
        "buildingName": "HMPPS Digital Studio",
        "addressNumber": "32",
        "streetName": "Scotland Street",
        "district": "Sheffield City Centre",
        "town": "Sheffield",
        "county": "South Yorkshire",
        "postcode": "S3 7BS"
    },
    "ethnicity": "Asian or Asian British: Pakistani",
    "disabilities": [],
    "language": {
        "requiresInterpreter": true,
        "primaryLanguage": "Urdu"
    },
    "personalCircumstances": [
        {
            "type": {
                "code": "G",
                "description": "Literacy and Numeracy"
            },
            "subType": {
                "code": "G01",
                "description": "Reading/Literacy Concerns"
            },
            "notes": "Comment added by Natalie Wood on 14/03/2023 at 10:06\nCannot read\n---------------------------------------------------------\nComment added by Natalie Wood on 14/03/2023 at 10:06\nNumeracy difficulties\n---------------------------------------------------------\nComment added by Natalie Wood on 14/03/2023 at 10:06\nCommunication difficulties",
            "evidenced": false
        },
        {
            "type": {
                "code": "D",
                "description": "General Health"
            },
            "subType": {
                "code": "D03",
                "description": "Allergies"
            },
            "notes": "Comment added by Natalie Wood on 14/03/2023 at 10:07\nNut Allergy",
            "evidenced": false
        },
        {
            "type": {
                "code": "I",
                "description": "Dependents"
            },
            "subType": {
                "code": "I02",
                "description": "Is a Primary Carer"
            },
            "notes": "Comment added by Natalie Wood on 14/03/2023 at 10:07\nPrimary Carer",
            "evidenced": true
        }
    ],
    "personalContacts": [
        {
            "relationship": "GP (secondary)",
            "relationshipType": {
                "code": "RT02",
                "description": "GP"
            },
            "name": {
                "forename": "Steve",
                "surname": "Wilson"
            },
            "mobileNumber": "0776 666 6666",
            "address": {
                "buildingName": "The Building",
                "addressNumber": "77",
                "streetName": "Some Street",
                "district": "Some City Centre",
                "town": "London",
                "county": "Essex",
                "postcode": "NW10 1EP"
            }
        },
        {
            "relationship": "Father",
            "relationshipType": {
                "code": "ME",
                "description": "Emergency Contact"
            },
            "name": {
                "forename": "Brian",
                "surname": "Smith"
            },
            "telephoneNumber": "0133456789",
            "mobileNumber": "07333567890",
            "address": {
                "addressNumber": "36",
                "streetName": "Fifth Street",
                "district": "South City Centre",
                "town": "London",
                "county": "South London",
                "postcode": "W10 4DN"
            }
        },
        {
            "relationship": "GP",
            "relationshipType": {
                "code": "RT02",
                "description": "GP"
            },
            "name": {
                "forename": "Nick",
                "surname": "Riviera"
            },
            "telephoneNumber": "0233456789",
            "address": {
                "buildingName": "The practice",
                "addressNumber": "38",
                "streetName": "East Street",
                "district": "East City Centre",
                "town": "Bristol",
                "county": "East London",
                "postcode": "E5 7BS"
            }
        }
    ],
    "registerFlags": [],
    "sentence": {
        "startDate": "2023-01-26",
        "mainOffence": {
            "category": {
                "code": "150",
                "description": "Merchant Shipping Acts"
            },
            "subCategory": {
                "code": "00",
                "description": "Merchant Shipping Acts"
            }
        }
    }
}
    """.trimIndent()
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
