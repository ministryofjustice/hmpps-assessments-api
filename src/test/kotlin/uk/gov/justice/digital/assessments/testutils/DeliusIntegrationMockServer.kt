package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class DeliusIntegrationMockServer : WireMockServer(9097) {

  fun stubGetCaseData() {
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/case-data/(?:DX5678A|X1356|CRN1|X1355|X1346)/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              crnWithPersonalContactsAndCircumstances(),
            ),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlPathMatching("/case-data/(?:DX5678A|X1356|CRN1|X1355|X1346)/1"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              crnWithPersonalContactsAndCircumstances(),
            ),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/DX5678B/1"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              crnWithPersonalContactsAndCircumstances(),
            ),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlPathMatching("/case-data/(?:invalidNotFound|X1404)/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"The case details are not found\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidBadRequest/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidUnauthorized/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidForbidden/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(403)
            .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/case-data/invalidNotKnow/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(422)
            .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}"),
        ),
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
    "disabilities": [
      {
        "type": {
          "code": "LDI",
          "description": "Learning Disability"
        },
        "condition": {
          "code": "LD2",
          "description": "Learning Disability Condition"
        },
        "notes": "notes about this disability"
      },
      {
        "type": {
          "code": "MHR",
          "description": "Mental Health related disabilities"
        },
        "condition": {
          "code": "MHR2",
          "description": "Mental Health related disabilities condition"
        },
        "notes": "notes about this disability"
      },
      {
        "type": {
          "code": "MRD",
          "description": "Mobility related Disabilities"
        },
        "notes": "notes about this disability"
      }
    ],
    "provisions": [
      {
        "type": {
          "code": "ACC1",
          "description": "Accessibility"
        },
        "category": {
          "code": "HAND1",
          "description": "Handrails"
        }
      }
    ],
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
    "mappaRegistration": {
      "startDate": "2022-01-01",
      "level": {
        "code": "M1",
        "description": "MAPPA Level 1"
      },
      "category": {
        "code": "M2",
        "description": "MAPPA Cat 2"
      }
    },
    "registerFlags": [
      {
        "code": "STRG",
        "description": "Street gangs",
        "riskColour": "Red"
      }
    ],
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

  fun stubGetUserAccess() {
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/users/([a-zA-Z0-9/-]*)/access/(?:X1|DX|CRN)[a-zA-Z0-9]*"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(laoSuccess),
        ),
    )
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/users/([a-zA-Z0-9/-]*)/access/(OX[a-zA-Z0-9]*)"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(laoFailure),
        ),
    )
    stubFor(
      WireMock.get(WireMock.urlPathMatching("/users/([a-zA-Z0-9/-]*)/access/invalidNotFound"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"message\":\"The offender is not found\"}"),
        ),
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/users/user1/access/invalidBadRequest"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"message\":\"invalidBadRequest\"}"),
        ),
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/users/user1/access/invalidNotAuthorised"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"message\":\"Not authorised\"}"),
        ),
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/users/user1/access/invalidNotKnow"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(422)
            .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}"),
        ),
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
