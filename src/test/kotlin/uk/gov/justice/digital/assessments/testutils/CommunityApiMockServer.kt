package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import uk.gov.justice.digital.assessments.restclient.communityapi.Address
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.ContactDetails
import uk.gov.justice.digital.assessments.restclient.communityapi.Disability
import uk.gov.justice.digital.assessments.restclient.communityapi.DisabilityType
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderLanguages
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderProfile
import uk.gov.justice.digital.assessments.restclient.communityapi.Phone
import uk.gov.justice.digital.assessments.restclient.communityapi.Type

class CommunityApiMockServer : WireMockServer(9096) {

  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubGetOffenderRegistrations() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/DX5678A/registrations"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              "{" +
                "    \"registrations\": [" +
                "        {" +
                "            \"registrationId\": 2500233278," +
                "            \"offenderId\": 2500275961," +
                "            \"register\": {" +
                "                \"code\": \"5\"," +
                "                \"description\": \"Public Protection\"" +
                "            }," +
                "            \"type\": {" +
                "                \"code\": \"MAPP\"," +
                "                \"description\": \"MAPPA\"" +
                "            }," +
                "            \"riskColour\": \"Red\"," +
                "            \"startDate\": \"2021-10-10\"," +
                "            \"nextReviewDate\": \"2022-01-10\"," +
                "            \"reviewPeriodMonths\": 3," +
                "            \"notes\": \"Please Note - Category 3 offenders require multi-agency management at Level 2 or 3 and should not be recorded at Level 1.\"," +
                "            \"registeringTeam\": {" +
                "                \"code\": \"N07UTS\"," +
                "                \"description\": \"Tiering Service\"" +
                "            }," +
                "            \"registeringOfficer\": {" +
                "                \"code\": \"N07UTSO\"," +
                "                \"forenames\": \"Tiering\"," +
                "                \"surname\": \"Service\"," +
                "                \"unallocated\": false" +
                "            }," +
                "            \"registeringProbationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"registerLevel\": {" +
                "                \"code\": \"M0\"," +
                "                \"description\": \"MAPPA Nominal (level to be determined)\"" +
                "            }," +
                "            \"registerCategory\": {" +
                "                \"code\": \"X9\"," +
                "                \"description\": \"All - Category to be determined\"" +
                "            }," +
                "            \"warnUser\": false," +
                "            \"active\": true," +
                "            \"numberOfPreviousDeregistrations\": 0" +
                "        }" +
                "    ]" +
                "}"
            )
        )
    )
  }

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
                "            \"lastUpdatedDatetime\": \"2021-10-25T12:08:42\"" +
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
                "        }," +
                "        {" +
                "            \"personalCircumstanceId\": 2500177517," +
                "            \"offenderId\": 2500275961," +
                "            \"personalCircumstanceType\": {" +
                "                \"code\": \"PM\"," +
                "                \"description\": \"Pregnancy/Maternity\"" +
                "            }," +
                "            \"personalCircumstanceSubType\": {" +
                "                \"code\": \"D06\"," +
                "                \"description\": \"Pregnancy\"" +
                "            }," +
                "            \"startDate\": \"2021-10-17\"," +
                "            \"probationArea\": {" +
                "                \"code\": \"N07\"," +
                "                \"description\": \"NPS London\"" +
                "            }," +
                "            \"evidenced\": true," +
                "            \"createdDatetime\": \"2021-10-21T11:32:33\"," +
                "            \"lastUpdatedDatetime\": \"2021-10-21T11:32:33\"" +
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
            .withBody(
              "[" +
                "{" +
                "\"personalContactId\": 2500124492, " +
                "\"relationship\": \"Father\", " +
                "\"startDate\": \"2021-10-22T00:00:00\", " +
                "\"title\": \"Mr\", " +
                "\"firstName\": \"Brian\", " +
                "\"surname\": \"Contact\", " +
                "\"gender\": \"Male\", " +
                "\"mobileNumber\": \"07333567890\", " +
                "\"relationshipType\": { " +
                "\"code\": \"ME\", " +
                "\"description\": \"Emergency Contact\" " +
                "},  " +
                "\"createdDatetime\": \"2021-10-22T10:24:14\",  " +
                "\"lastUpdatedDatetime\": \"2021-10-22T10:24:14\",  " +
                "\"address\": {" +
                "\"addressNumber\": \"36\",  " +
                "\"buildingName\": \"HMPPS Studio\", " +
                "\"county\": \"South London\",  " +
                "\"district\": \"South City Centre\"," +
                "\"postcode\": \"S4 7BS\",  " +
                "\"streetName\": \"Fifth Street\"," +
                "\"telephoneNumber\": \"0133456789\", " +
                "\"town\": \"London\" " +
                "}   " +
                "},    " +
                "{   " +
                "\"personalContactId\": 2500123992, " +
                "\"relationship\": \"Family Doctor\", " +
                "\"startDate\": \"2021-10-21T00:00:00\", " +
                "\"title\": \"Dr\", " +
                "\"firstName\": \"Nick\", " +
                "\"surname\": \"Riviera\", " +
                "\"gender\": \"Male\", " +
                "\"relationshipType\": { " +
                "\"code\": \"RT02\", " +
                "\"description\": \"GP\" " +
                "}, " +
                "\"createdDatetime\": \"2021-10-21T15:02:53\", " +
                "\"lastUpdatedDatetime\": \"2021-10-21T15:02:53\", " +
                "\"address\": {" +
                "\"addressNumber\": \"38\",  " +
                "\"buildingName\": \"The practice\", " +
                "\"county\": \"East London\",  " +
                "\"district\": \"East City Centre\"," +
                "\"postcode\": \"E5 7BS\",  " +
                "\"streetName\": \"East Street\"," +
                "\"telephoneNumber\": \"0233456789\", " +
                "\"town\": \"Bristol\" " +
                "}     " +
                "}]"
            )
        )
    )
  }

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
          Phone("1838893", "MOBILE"),
          Phone("0123456999", "TELEPHONE")
        ),
        addresses = listOf(
          Address(
            addressNumber = "32",
            buildingName = "HMPPS Digital Studio",
            county = "South Yorkshire",
            district = "Sheffield City Centre",
            postcode = "S3 7BS",
            status = Type(
              code = "M", description = "Main address"
            ),
            streetName = "Scotland Street",
            telephoneNumber = "0123456999",
            town = "Sheffield"
          ),
          Address(
            addressNumber = "33",
            buildingName = "HMPPS Digital Studio 2",
            county = "South London",
            district = "London City Centre",
            postcode = "S3 8BS",
            status = Type(
              code = "O", description = "Some description"
            ),
            streetName = "Pink Street",
            telephoneNumber = "0123456998",
            town = "London"
          )
        )
      ),
      offenderProfile = OffenderProfile(
        ethnicity = "Asian",
        disabilities = listOf(
          Disability(DisabilityType("D", "general health")),
          Disability(DisabilityType("D02", "physical health concerns")),
          Disability(DisabilityType("RM", "reduced mobility")),
          Disability(DisabilityType("RC", "reduced physical capacity")),
          Disability(DisabilityType("PC", "progressive condition")),
          Disability(DisabilityType("VI", "visual impairment")),
          Disability(DisabilityType("HD", "hearing difficulties")),
          Disability(DisabilityType("LA", "learning disability")),
          Disability(DisabilityType("LD", "learning difficulties")),
          Disability(DisabilityType("D01", "mental health")),
          Disability(DisabilityType("MI", "mental illness"))
        ),
        offenderLanguages = OffenderLanguages("French", true)
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
