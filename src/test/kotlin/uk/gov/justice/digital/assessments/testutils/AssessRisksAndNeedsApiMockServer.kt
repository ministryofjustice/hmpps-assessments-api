package uk.gov.justice.digital.assessments.testutils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import uk.gov.justice.digital.assessments.api.RoshRiskSummaryDto
import java.time.LocalDate
import java.util.UUID

class AssessRisksAndNeedsApiMockServer : WireMockServer(9007) {
  private val objectMapper: ObjectMapper = jacksonObjectMapper().registerModules(JavaTimeModule())

  fun stubGetRSRPredictorsForOffenderAndOffencesWithCurrentOffences(
    final: Boolean,
    episodeUuid: UUID,
    crn: String,
    offenceCode: String = "138",
    offenceSubCode: String = "00",
  ) {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/risks/predictors/RSR?final=$final&source=ASSESSMENTS_API&sourceId=$episodeUuid"))
        .withRequestBody(
          WireMock.equalToJson(
            "{ " +
              "\"crn\": \"$crn\"," +
              "\"gender\" : \"MALE\"," +
              "\"dob\" : [ 2001, 1, 1 ]," +
              "\"assessmentDate\" : [ 2021, 1, 1, 0, 0 ]," +
              "\"currentOffence\" : {" +
              "    \"offenceCode\" : \"$offenceCode\"," +
              "    \"offenceSubcode\" : \"$offenceSubCode\"" +
              "}," +
              "\"dateOfFirstSanction\" : \"2020-01-01\"," +
              "\"totalOffences\" : 10," +
              "\"totalViolentOffences\" : 8," +
              "\"dateOfCurrentConviction\" : \"2020-12-18\"," +
              "\"hasAnySexualOffences\" : true," +
              "\"isCurrentSexualOffence\" : true," +
              "\"isCurrentOffenceVictimStranger\" : true," +
              "\"mostRecentSexualOffenceDate\" : \"2020-12-11\"," +
              "\"totalSexualOffencesInvolvingAnAdult\" : 5," +
              "\"totalSexualOffencesInvolvingAChild\" : 3," +
              "\"totalSexualOffencesInvolvingChildImages\" : 2," +
              "\"totalNonContactSexualOffences\" : 2," +
              "\"earliestReleaseDate\" : \"2021-11-01\"," +
              "\"hasCompletedInterview\" : true," +
              "\"dynamicScoringOffences\" : {" +
              "   \"hasSuitableAccommodation\" : \"MISSING\"," +
              "   \"employment\": \"NOT_AVAILABLE_FOR_WORK\"," +
              "   \"currentRelationshipWithPartner\" : \"SIGNIFICANT_PROBLEMS\"," +
              "   \"evidenceOfDomesticViolence\" : true," +
              "   \"isPerpetrator\" : true," +
              "   \"alcoholUseIssues\" : \"SIGNIFICANT_PROBLEMS\"," +
              "   \"bingeDrinkingIssues\" : \"SIGNIFICANT_PROBLEMS\"," +
              "   \"impulsivityIssues\" : \"SOME_PROBLEMS\"," +
              "   \"temperControlIssues\" : \"SIGNIFICANT_PROBLEMS\"," +
              "   \"proCriminalAttitudes\" : \"SOME_PROBLEMS\"," +
              "   \"previousOffences\" : {" +
              "   \"murderAttempt\" : true," +
              "   \"wounding\" : true," +
              "   \"aggravatedBurglary\" : true," +
              "   \"arson\" : true," +
              "   \"criminalDamage\" : true," +
              "   \"kidnapping\" : true," +
              "   \"firearmPossession\" : true," +
              "   \"robbery\" : true," +
              "   \"offencesWithWeapon\" : true" +
              "   }," +
              "   \"currentOffences\" : { " +
              "   \"firearmPossession\" :true, " +
              "   \"offencesWithWeapon\" : true " +
              "   } " +
              "}" +
              "}",
            true,
            true,
          ),
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(riskRsrPredictors),
        ),
    )
  }

  fun stubGetRoshRiskSummary() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/risks/crn/DX12340A/widget"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(
              mapToJson(
                RoshRiskSummaryDto(
                  assessedOn = LocalDate.parse("2021-10-10"),
                  riskInCommunity = mapOf(
                    "Public" to "HIGH",
                    "Known Adult" to "MEDIUM",
                    "Staff" to "MEDIUM",
                    "Children" to "LOW",
                  ),
                ),
              ),
            ),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/risks/crn/invalidNotFound/widget"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"The offender is not found\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/risks/crn/invalidBadRequest/widget"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(400)
            .withBody("{\"status\":\"400\",\"developerMessage\":\"Invalid CRN invalidBadRequest\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/risks/crn/invalidUnauthorized/widget"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(401)
            .withBody("{\"status\":\"401\",\"developerMessage\":\"Not authorised\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/risks/crn/invalidForbidden/widget"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(403)
            .withBody("{\"status\":\"403\",\"developerMessage\":\"Forbidden\"}"),
        ),
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/risks/crn/invalidNotKnow/widget"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(422)
            .withBody("{\"status\":\"422\",\"developerMessage\":\"unprocessable\"}"),
        ),
    )
  }

  val riskRsrPredictors =
    """{
        "algorithmVersion": 3,
        "calculatedAt": "2021-08-09 14:46:48", 
        "type": "RSR",
        "scoreType": "STATIC",
        "scores": {
          "RSR": {"level": "HIGH", "score": 11.34, "isValid": true},
          "OSPC":{"level": "NOT_APPLICABLE", "score": 0, "isValid" : false},
          "OSPI":{"level": "NOT_APPLICABLE", "score": 0, "isValid" : false}
          }
        }
    """.trimIndent()

  private fun mapToJson(dto: Any): String {
    return objectMapper.writeValueAsString(dto)
  }
}
