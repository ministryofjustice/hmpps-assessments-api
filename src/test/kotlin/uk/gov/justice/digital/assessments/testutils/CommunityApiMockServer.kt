package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistration
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistrationElement
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityRegistrations
import java.time.LocalDate

class CommunityApiMockServer : WireMockServer(9096) {

  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubGetOffenderPersonalCircumstances() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/personalCircumstances"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              "{" +
                "    \"personalCircumstances\": [" +
                "       {" +
                "            \"personalCircumstanceId\": 2500178503," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"G\"," +
                "                \"description\": \"Literacy and Numeracy\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"G01\"," +
                "                \"description\": \"Reading/Literacy Concerns\"" +
                "            }," +
                "            \"startDate\": \"2021-08-09\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"Cannot read\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"isActive\": true" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2507778503," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"G\"," +
                "                \"description\": \"Literacy and Numeracy\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"G02\"," +
                "                \"description\": \"Numeracy Concerns\"" +
                "            }," +
                "            \"startDate\": \"2021-08-09\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"Numeracy difficulties\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"isActive\": true" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2503338503," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"G\"," +
                "                \"description\": \"Literacy and Numeracy\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"G03\"," +
                "                \"description\": \"Language/Communication Concerns\"" +
                "            }," +
                "            \"startDate\": \"2021-08-09\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"Communication difficulties\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"isActive\": true" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500178003," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"D\"," +
                "                \"description\": \"General Health\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"D03\"," +
                "                \"description\": \"Allergies\"" +
                "            }," +
                "            \"startDate\": \"2021-10-03\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"Nut Allergy\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-22T09:26:44\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-22T09:26:44\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177526," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"A\"," +
                "                \"description\": \"Accommodation\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"A03\"," +
                "                \"description\": \"Transient/short term accommodation\"" +
                "            }," +
                "            \"startDate\": \"2021-04-26\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-21T14:58:23\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:58:23\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177525," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"B\"," +
                "                \"description\": \"Employment\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"B01C\"," +
                "                \"description\": \"Full-time employed (30 or more hours per week)\"" +
                "            }," +
                "            \"startDate\": \"2021-10-12\"," +
                "            \"endDate\": \"2022-03-18\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"evidenced\": false," +
                "            \"createdDatetime\": \"2021-10-21T14:53:10\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:53:10\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177535," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"B\"," +
                "                \"description\": \"Employment\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"B02C\"," +
                "                \"description\": \"Part-time employed (15 hours per week)\"" +
                "            }," +
                "            \"startDate\": \"2021-10-12\"," +
                "            \"endDate\": \"2022-03-18\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"evidenced\": false," +
                "            \"createdDatetime\": \"2021-10-21T14:53:10\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:53:10\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177545," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"B\"," +
                "                \"description\": \"Employment\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"B09A\"," +
                "                \"description\": \"Not employed\"" +
                "            }," +
                "            \"startDate\": \"2021-10-12\"," +
                "            \"endDate\": \"2022-03-18\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"evidenced\": false," +
                "            \"createdDatetime\": \"2021-10-21T14:53:10\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:53:10\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177524," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"I\"," +
                "                \"description\": \"Dependents\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"I02\"," +
                "                \"description\": \"Is a Primary Carer\"" +
                "            }," +
                "            \"startDate\": \"2021-10-04\"," +
                "            \"endDate\": \"2022-03-02\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"Primary Carer\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-21T14:52:46\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:52:46\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177523," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"OCA\"," +
                "                \"description\": \"Offender is carer of adult with care & sup't needs\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"OCA04\"," +
                "                \"description\": \"Carer Support Plan in Place\"" +
                "            }," +
                "            \"startDate\": \"2021-10-03\"," +
                "            \"endDate\": \"2022-01-20\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"testtest\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-21T14:51:44\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:52:10\"" +
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177522," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"H\"," +
                "                \"description\": \"Relationship\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"H03\"," +
                "                \"description\": \"Divorced / Dissolved\"" +
                "            }," +
                "            \"startDate\": \"2021-10-03\"," +
                "            \"endDate\": \"2021-10-21\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-21T14:51:06\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T14:51:06\"" +
                "        }" +
                "    ]" +
                "}"
            )
        )
    )
  }

  fun stubGetOffenderPersonalCircumstancesPregnant() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678B/personalCircumstances"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              "{" +
                "    \"personalCircumstances\": [" +
                "       {" +
                "            \"personalCircumstanceId\": 2500178503," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"PM\"," +
                "                \"description\": \"Pregnant\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"D06\"," +
                "                \"description\": \"Pregnant\"" +
                "            }," +
                "            \"startDate\": \"2021-08-09\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"notes\": \"Some notes\"," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-25T12:08:42\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-25T12:08:42\"" +
                "        }" +
                "    ]" +
                "}"
            )
        )
    )
  }

  fun stubGetOffenderPersonalContacts() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/personalContacts"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(personalContact)
        )
    )
  }

  fun stubGetOffenderPersonalContactsPregnant() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678B/personalContacts"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(personalContact)
        )
    )
  }

  fun stubGetOffender() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340A/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderDto("DX12340A"))
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340F/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderDto("DX12340F"))
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1355/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderDto("X1355"))
        )
    )
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/X1356/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderDto("X1356"))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderDto("DX5678A"))
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678B/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderDto("DX5678B"))
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

  fun stubGetOffenderRegistrations() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX12340A/registrations"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              mapToJson(
                CommunityRegistrations(
                  listOf(
                    CommunityRegistration(
                      active = true,
                      warnUser = true,
                      riskColour = "Red",
                      registerCategory = CommunityRegistrationElement("M2", "MAPPA Cat 2"),
                      registerLevel = CommunityRegistrationElement("M1", "MAPPA Level 1"),
                      type = CommunityRegistrationElement("MAPP", "MAPPA"),
                      startDate = LocalDate.parse("2021-10-10"),
                    ),
                    CommunityRegistration(
                      active = true,
                      warnUser = true,
                      riskColour = "Red",
                      registerCategory = CommunityRegistrationElement("RC12", "Hate Crime - Disability"),
                      type = CommunityRegistrationElement("IRMO", "Hate Crime"),
                      startDate = LocalDate.parse("2021-10-10"),
                    ),
                    CommunityRegistration(
                      active = true,
                      warnUser = false,
                      riskColour = "Red",
                      type = CommunityRegistrationElement("RHRH", "High RoSH"),
                      startDate = LocalDate.parse("2021-10-10"),
                    ),
                  )
                )
              )
            )
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/registrations"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidBadRequest/registrations"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidUnauthorized/registrations"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidForbidden/registrations"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(403)
            .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}")
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotKnow/registrations"))
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
      WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1|DX|CRN)[a-zA-Z0-9]*/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionsJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlPathMatching("/secure/offenders/crn/(?:X1|DX|CRN)[a-zA-Z0-9]*/convictions/123456"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalidNotFound/convictions"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
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

  private fun mapToJson(dto: Any): String {
    return objectMapper.writeValueAsString(dto)
  }

  private fun offenderDto(crn: String): String {
    return """
      {
          "offenderId": 101,
          "title": "Mr",
          "firstName": "John",
          "middleNames": [
              "firstMiddleName",
              "secondMiddleName"
          ],
          "surname": "Smith",
          "dateOfBirth": "1979-08-18",
          "gender": "Male",
          "otherIds": {
              "crn": "$crn",
              "pncNumber": "A/1234560BA"
          },
          "contactDetails": {
              "phoneNumbers": [
                  {
                      "type": "TELEPHONE",
                      "number": "0123456999"
                  },
                  {
                      "type": "MOBILE",
                      "number": "071838893"
                  }
              ],
              "emailAddresses": [
                  "address1@gmail.com",
                  "address2@gmail.com"
              ],
              "allowSMS": false,
              "addresses": [
                  {
                      "from": "2022-05-23",
                      "noFixedAbode": false,
                      "addressNumber": "32",
                      "buildingName": "HMPPS Digital Studio",
                      "streetName": "Scotland Street",
                      "district": "Sheffield City Centre",
                      "town": "Sheffield",
                      "county": "South Yorkshire",
                      "postcode": "S3 7BS",
                      "telephoneNumber": "0123456999",
                      "status": {
                          "code": "M",
                          "description": "Main"
                      },
                      "typeVerified": false,
                      "createdDatetime": "2022-05-23T11:57:22",
                      "lastUpdatedDatetime": "2022-05-23T11:59:19"
                  },
                  {
                      "from": "2022-05-23",
                      "noFixedAbode": false,
                      "addressNumber": "33",
                      "buildingName": "HMPPS Digital Studio 2",
                      "streetName": "Pink Street",
                      "district": "London City Centre",
                      "town": "London",
                      "county": "South London",
                      "postcode": "S3 8BS",
                      "telephoneNumber": "0123456998",
                      "status": {
                          "code": "P",
                          "description": "Previous"
                      },
                      "typeVerified": false,
                      "createdDatetime": "2022-05-23T11:58:39",
                      "lastUpdatedDatetime": "2022-05-23T11:58:39"
                  }
              ]
          },
          "offenderProfile": {
              "offenderLanguages": {},
              "previousConviction": {},
              "ethnicity": "Asian",
              "genderIdentity": "Non-Binary",
              "offenderLanguages": {
                  "primaryLanguage": "French",
                  "requiresInterpreter": true
              },
              "disabilities": [
                  {
                      "disabilityId": 2500096591,
                      "disabilityType": {
                          "code": "MI",
                          "description": "Mental Illness"
                      },
                      "startDate": "2022-05-22",
                      "notes": "Comment added by Natalie Wood on 23/05/2022 at 12:05\nHas depression",
                      "provisions": [
                          {
                              "provisionId": 2500081161,
                              "startDate": "2022-05-22",
                              "provisionType": {
                                  "code": "12",
                                  "description": "Behavioural responses/Body language"
                              }
                          }
                      ],
                      "lastUpdatedDateTime": "2022-05-23T12:05:44",
                      "isActive": true
                  },
                  {
                      "disabilityId": 2500096589,
                      "disabilityType": {
                          "code": "VI",
                          "description": "Visual Impairment"
                      },
                      "startDate": "2022-05-22",
                      "notes": "Comment added by Natalie Wood on 23/05/2022 at 12:03\nBlind in the left eye\n---------------------------------------------------------\nComment added by Natalie Wood on 23/05/2022 at 12:05\nPartially sighted in the right eye\n---------------------------------------------------------\nComment added by Natalie Wood on 23/05/2022 at 12:05\nCataracts",
                      "provisions": [
                          {
                              "provisionId": 2500081158,
                              "startDate": "2022-05-22",
                              "provisionType": {
                                  "code": "09",
                                  "description": "Improved signage"
                              }
                          },
                          {
                              "provisionId": 2500081160,
                              "startDate": "2022-05-22",
                              "provisionType": {
                                  "code": "02",
                                  "description": "Audio/Braille/Moon"
                              }
                          },
                          {
                              "provisionId": 2500081161,
                              "startDate": "2100-05-22",
                              "notes": "Should be filtered because it has not started",
                              "provisionType": {
                                  "code": "00",
                                  "description": "Not started provision"
                              }
                          },                   
                          {
                              "provisionId": 2500081161,
                              "startDate": "2022-05-22",
                              "finishDate": "1900-01-01",
                              "notes": "Should be filtered because it is expired",
                              "provisionType": {
                                  "code": "00",
                                  "description": "Expired provision"
                              }
                          }
                      ],
                      "lastUpdatedDateTime": "2022-05-23T12:05:23",
                      "isActive": true
                  },
                  {
                      "disabilityId": 2500096590,
                      "disabilityType": {
                          "code": "RM",
                          "description": "Reduced Mobility"
                      },
                      "startDate": "2022-05-22",
                      "notes": "Comment added by Natalie Wood on 23/05/2022 at 12:04\nStiff arm",
                      "provisions": [
                          {
                              "provisionId": 2500081159,
                              "startDate": "2022-05-22",
                              "provisionType": {
                                  "code": "22",
                                  "description": "Handrails"
                              }
                          }
                      ],
                      "lastUpdatedDateTime": "2022-05-23T12:04:19",
                      "isActive": true
                  }
              ]
          },
          "offenderAliases": [
              {
                  "id": "2500120187",
                  "dateOfBirth": "1979-09-18",
                  "firstName": "John",
                  "surname": "Smithy",
                  "gender": "Male"
              },
              {
                  "id": "2500120188",
                  "dateOfBirth": "1979-08-18",
                  "firstName": "Jonny",
                  "gender": "Male"
              }
          ],
          "softDeleted": false,
          "currentDisposal": "0",
          "partitionArea": "National Data",
          "currentRestriction": false,
          "restrictionMessage": "This is a restricted offender record. Please contact a system administrator",
          "currentExclusion": false,
          "exclusionMessage": "You are excluded from viewing this offender record. Please contact a system administrator",
          "currentTier": "D-0",
          "activeProbationManagedSentence": false
      }
    """.trimIndent()
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

  private val convictionJson = """
    {
        "convictionId": 123456,
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
    }
"""

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

private const val personalContact = """[
  {
    "personalContactId": 2500124492,
    "relationship": "Father",
    "startDate": "2021-10-22T00:00:00",
    "title": "Mr",
    "firstName": "Brian",
    "surname": "Contact",
    "mobileNumber": "07333567890",
    "emailAddress": "test@test.com",
    "notes": "ARN Mapping Value testing - 28/10/2022 - ARN-631",
    "gender": "Male",
    "relationshipType": {
      "code": "ME",
      "description": "Emergency Contact"
    },
    "createdDatetime": "2021-10-22T10:24:14",
    "lastUpdatedDatetime": "2021-10-22T10:24:14",
    "address": {
      "addressNumber": "36",
      "buildingName": "HMPPS Studio",
      "streetName": "Fifth Street",
      "district": "South City Centre",
      "town": "London",
      "county": "South London",
      "postcode": "South City Centre",
      "telephoneNumber": "0133456789"
    },
    "isActive": true
  },
  {
    "personalContactId": 2500123992,
    "relationship": "Family Doctor",
    "startDate": "2021-10-21T00:00:00",
    "title": "Dr",
    "firstName": "Nick",
    "surname": "Riviera",
    "mobileNumber": "07123456789",
    "emailAddress": "gp@gp.com",
    "notes": "ARN Mapping Value testing - 28/10/2022 - ARN-631",
    "gender": "Male",
    "relationshipType": {
      "code": "RT02",
      "description": "GP"
    },
    "createdDatetime": "2021-10-21T15:02:53",
    "lastUpdatedDatetime": "2021-10-21T15:02:53",
    "address": {
      "addressNumber": "38",
      "buildingName": "The practice",
      "streetName": "East Street",
      "district": "East City Centre",
      "town": "Bristol",
      "county": "East London",
      "postcode": "E5 7BS",
      "telephoneNumber": "0233456789"
    },
    "isActive": true
  },
  {
    "personalContactId": 2500123992,
    "relationship": "Family Doctor",
    "startDate": "2021-10-21T00:00:00",
    "title": "Dr",
    "firstName": "Steve",
    "surname": "Wilson",
    "mobileNumber": "0779 786 5666",
    "emailAddress": "steve@gp.com",
    "notes": "ARN Mapping Value testing - 28/10/2022 - ARN-770",
    "gender": "Male",
    "relationshipType": {
      "code": "RT02",
      "description": "GP"
    },
    "createdDatetime": "2021-10-21T15:02:53",
    "lastUpdatedDatetime": "2021-10-21T15:02:53",
    "address": {
      "addressNumber": "77",
      "buildingName": "The Building",
      "streetName": "Some Street",
      "district": "Some City Centre",
      "town": "London",
      "county": "Essex",
      "postcode": "NW10 1EP",
      "telephoneNumber": "0776 666 6666"
    },
    "isActive": true
  },
  {
    "personalContactId": 2500123992,
    "relationship": "Family Doctor",
    "startDate": "2021-10-21T00:00:00",
    "title": "Dr",
    "firstName": "Henry",
    "surname": "Jekyll",
    "mobileNumber": "0779 786 5666",
    "emailAddress": "Ed@gp.com",
    "notes": "ARN Mapping Value testing - 28/10/2022 - ARN-770",
    "gender": "Male",
    "relationshipType": {
      "code": "RT02",
      "description": "GP"
    },
    "createdDatetime": "2021-10-21T15:02:53",
    "lastUpdatedDatetime": "2021-10-21T15:02:53",
    "address": {
      "addressNumber": "77",
      "buildingName": "The Building",
      "streetName": "Some Street",
      "district": "Some City Centre",
      "town": "London",
      "county": "Essex",
      "postcode": "NW10 1EP",
      "telephoneNumber": "0776 666 6666"
    },
    "isActive": false
  }
]"""
