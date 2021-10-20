package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.ContactDetails
import uk.gov.justice.digital.assessments.restclient.communityapi.Disability
import uk.gov.justice.digital.assessments.restclient.communityapi.DisabilityType
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderProfile
import uk.gov.justice.digital.assessments.restclient.communityapi.Phone

class CommunityApiMockServer : WireMockServer(9096) {

  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubGetOffender() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340A/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(mapToJson(offenderDto("DX12340A")))
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340F/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(mapToJson(offenderDto("DX12340F")))
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1355/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(mapToJson(offenderDto("X1355")))
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1356/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(mapToJson(offenderDto("X1356")))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(mapToJson(offenderDto("DX5678A")))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidBadRequest/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidUnauthorized/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidForbidden/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(403)
            .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotKnow/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(422)
            .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}")
        )
    )
  }

  fun stubGetConvictions() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340A/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionsJson)
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionsJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1355/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionsJson)
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1356/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionsJson)
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/CRN1/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionsJson)
        )
    )
  }

  fun stubGetPrimaryIds() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/primaryIdentifiers?includeActiveOnly=true&page=0&size=100"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(primaryIdsJson)
        )
    )
  }

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
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/user/user1/userAccess"))
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

  private fun mapToJson(dto: Any): String {
    return objectMapper.writeValueAsString(dto)
  }

  private fun offenderDto(crn: String): CommunityOffenderDto {
    return CommunityOffenderDto(
      offenderId = 101L,
      firstName = "John",
      middleNames = listOf("firstMiddleName", "secondMiddleName"),
      surname = "Smith",
      previousSurname = null,
      dateOfBirth = "1979-08-18",
      gender = "F",
      otherIds = IDs(
        crn = crn,
        pncNumber = "A/1234560BA"
      ),
      offenderAliases = listOf(
        OffenderAlias(
          firstName = "John",
          surname = "Smithy",
          dateOfBirth = "1979-09-18"
        ),
        OffenderAlias(
          firstName = "Jonny"
        )
      ),
      contactDetails = ContactDetails(
        emailAddresses = listOf("address1@gmail.com", "address2@gmail.com"),
        phoneNumbers = listOf(
          Phone("1838893", "MOBILE")
        )
      ),
      offenderProfile = OffenderProfile(
        ethnicity = "Asian",
        disabilities = listOf(Disability(DisabilityType("LA", "learning disability")),
          Disability(DisabilityType("LD", "learning difficulties")),
          Disability(DisabilityType("D", "general health")),
          Disability(DisabilityType("D01", "mental health")),
          Disability(DisabilityType("MI", "mental illness")))
      )
    )
  }

  private val convictionsJson = """[
    {
        "convictionId": 2500000223,
        "index": "1",
        "active": true,
        "inBreach": false,
        "failureToComplyCount": 0,
        "awaitingPsr": false,
        "referralDate": "2013-10-26",
        "offences": [
            {
                "offenceId": "M2500000223",
                "mainOffence": true,
                "detail": {
                    "code": "04600",
                    "description": "Stealing from shops and stalls (shoplifting) - 04600",
                    "mainCategoryCode": "046",
                    "mainCategoryDescription": "Stealing from shops and stalls (shoplifting)",
                    "mainCategoryAbbreviation": "Stealing from shops and stalls (shoplifting)",
                    "ogrsOffenceCategory": "Theft (Non-motor)",
                    "subCategoryCode": "00",
                    "subCategoryDescription": "Stealing from shops and stalls (shoplifting)",
                    "form20Code": "52"
                },
                "offenceDate": "2013-03-06T00:00:00",
                "offenceCount": 1,
                "offenderId": 2500000784,
                "createdDatetime": "1900-01-01T00:00:00",
                "lastUpdatedDatetime": "1900-01-01T00:00:00"
            }
        ],
        "sentence": {
            "sentenceId": 2500000179,
            "description": "CJA - Community Order",
            "originalLength": 12,
            "originalLengthUnits": "Months",
            "defaultLength": 12,
            "lengthInDays": 365,
            "unpaidWork": {
                "minutesOrdered": 6000,
                "minutesCompleted": 0,
                "appointments": {
                    "total": 0,
                    "attended": 0,
                    "acceptableAbsences": 0,
                    "unacceptableAbsences": 0,
                    "noOutcomeRecorded": 0
                },
                "status": "Being Worked"
            },
            "startDate": "2014-08-25",
            "sentenceType": {
                "code": "SP",
                "description": "CJA - Community Order"
            },
            "failureToComplyLimit": 2
        },
        "latestCourtAppearanceOutcome": {
            "code": "201",
            "description": "CJA - Community Order"
        },
        "courtAppearance": {
            "courtAppearanceId": 2500000265,
            "appearanceDate": "2013-10-26T00:00:00",
            "courtCode": "CWMBMC",
            "courtName": "Cwmbran Magistrates Court",
            "appearanceType": {
                "code": "S",
                "description": "Sentence"
            },
            "crn": "D001305"
        }
    },
    {
        "convictionId": 2500000223,
        "index": "12345",
        "active": true,
        "inBreach": false,
        "failureToComplyCount": 0,
        "awaitingPsr": false,
        "referralDate": "2013-10-26",
        "offences": [
            {
                "offenceId": "M2500000223",
                "mainOffence": true,
                "detail": {
                    "code": "04600",
                    "description": "Stealing from shops and stalls (shoplifting) - 04600",
                    "mainCategoryCode": "046",
                    "mainCategoryDescription": "Stealing from shops and stalls (shoplifting)",
                    "mainCategoryAbbreviation": "Stealing from shops and stalls (shoplifting)",
                    "ogrsOffenceCategory": "Theft (Non-motor)",
                    "subCategoryCode": "00",
                    "subCategoryDescription": "Stealing from shops and stalls (shoplifting)",
                    "form20Code": "52"
                },
                "offenceDate": "2013-03-06T00:00:00",
                "offenceCount": 1,
                "offenderId": 2500000784,
                "createdDatetime": "1900-01-01T00:00:00",
                "lastUpdatedDatetime": "1900-01-01T00:00:00"
            }
        ],
        "sentence": {
            "sentenceId": 2500000179,
            "description": "CJA - Community Order",
            "originalLength": 12,
            "originalLengthUnits": "Months",
            "defaultLength": 12,
            "lengthInDays": 365,
            "unpaidWork": {
                "minutesOrdered": 6000,
                "minutesCompleted": 0,
                "appointments": {
                    "total": 0,
                    "attended": 0,
                    "acceptableAbsences": 0,
                    "unacceptableAbsences": 0,
                    "noOutcomeRecorded": 0
                },
                "status": "Being Worked"
            },
            "startDate": "2014-08-25",
            "sentenceType": {
                "code": "SP",
                "description": "CJA - Community Order"
            },
            "failureToComplyLimit": 2
        },
        "latestCourtAppearanceOutcome": {
            "code": "201",
            "description": "CJA - Community Order"
        },
        "courtAppearance": {
            "courtAppearanceId": 2500000265,
            "appearanceDate": "2013-10-26T00:00:00",
            "courtCode": "CWMBMC",
            "courtName": "Cwmbran Magistrates Court",
            "appearanceType": {
                "code": "S",
                "description": "Sentence"
            },
            "crn": "CRN1"
        }
    }
]"""

  private val primaryIdsJson = """
    {
      "content": [
        {
          "offenderId": 2500000501,
          "crn": "D001022"
        },
        {
          "offenderId": 2500000519,
          "crn": "D001040"
        },
        {
          "offenderId": 2500000536,
          "crn": "DX12340A"
        }
      ],
      "pageable": {
        "sort": {
          "sorted": true,
          "unsorted": false,
          "empty": false
        },
        "offset": 0,
        "pageSize": 10,
        "pageNumber": 0,
        "unpaged": false,
        "paged": true
      },
      "last": false,
      "totalPages": 1090,
      "totalElements": 10893,
      "size": 10,
      "number": 0,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "first": true,
      "numberOfElements": 10,
      "empty": false
    }
  """.trimIndent()

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
