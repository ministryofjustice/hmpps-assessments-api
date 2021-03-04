package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityConvictionDto
import uk.gov.justice.digital.assessments.restclient.communityapi.CommunityOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import uk.gov.justice.digital.assessments.restclient.communityapi.Offence
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenceDetail
import uk.gov.justice.digital.assessments.restclient.communityapi.OffenderAlias
import java.time.LocalDate

@Component
class CommunityApiMockServer : WireMockServer(9096) {

  val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubGetOffender() {
    val crn = "DX12340A"
    val offenderJson = mapToJson(offenderDto())
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/$crn/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(offenderJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalid/all"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"Error\",\"message\":\"Offender not found\"}")
        )
    )
  }

  fun stubGetConvictions() {
    val crn = "DX12340A"
    val convictionId = "636401162"
    val convictionJson = mapToJson(convictionDto())
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/$crn/convictions/$convictionId"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(convictionJson)
        )
    )
  }

  internal fun mapToJson(dto: Any): String {
    return objectMapper.writeValueAsString(dto)
  }

  private fun offenderDto(): CommunityOffenderDto {
    return CommunityOffenderDto(
      offenderId = 101L,
      firstName = "John",
      middleNames = null,
      surname = "Smith",
      previousSurname = null,
      dateOfBirth = LocalDate.of(1979, 8, 18),
      gender = "F",
      otherIds = IDs(
        crn = "DX12340A",
        pncNumber = "A/1234560BA"
      ),
      offenderAliases = listOf(
        OffenderAlias(
          firstName = "John",
          surname = "Smithy"
        ),
        OffenderAlias(
          firstName = "Jonny"
        )
      )
    )
  }

  private fun convictionDto(): CommunityConvictionDto {
    return CommunityConvictionDto(
      convictionId = 636401162L,
      offences = listOf(
        Offence(
          offenceId = "offence1",
          mainOffence = true,
          detail = OffenceDetail(
            code = "code1",
            description = "Offence description"
          )
        ),
        Offence(
          offenceId = "offence2",
          mainOffence = false,
          detail = OffenceDetail(
            code = "code2",
            description = "Offence description"
          )
        )
      ),
      convictionDate = LocalDate.of(2020, 2, 1)
    )
  }
}
