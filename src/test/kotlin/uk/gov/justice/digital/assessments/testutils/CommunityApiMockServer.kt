package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.communityapi.GetOffenderDto
import uk.gov.justice.digital.assessments.restclient.communityapi.IDs
import java.time.LocalDate
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@Component
class CommunityApiMockServer: WireMockServer(9096) {

    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

    fun stubGetOffender() {
        val crn = "DX12340A"
        val offenderJson = mapToJson(getOffenderDto())
        stubFor(
            WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/$crn"))
                .willReturn(WireMock.aResponse()
                    .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
                    .withBody(offenderJson))
        )

        stubFor(
            WireMock.get(WireMock.urlEqualTo("/secure/offenders/crn/invalid"))
                .willReturn(WireMock.aResponse()
                    .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
                    .withStatus(404)
                    .withBody("{\"status\":\"Error\",\"message\":\"Offender not found\"}")
                )
        )
    }

    internal fun mapToJson(offenderDto: GetOffenderDto): String {
    return objectMapper.writeValueAsString(getOffenderDto())
    }

    private fun getOffenderDto(): GetOffenderDto{
        return GetOffenderDto(
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
            )
        )
    }
}
