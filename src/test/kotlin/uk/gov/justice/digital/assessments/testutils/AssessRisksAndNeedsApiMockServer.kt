package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class AssessRisksAndNeedsApiMockServer : WireMockServer(9007) {
  fun stubGetRSRPredictorsForOffenderAndOffences() {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/risks/predictors/RSR"))
        .withRequestBody(
          WireMock.equalToJson(
            "{ " +
              "\"crn\": \"X1345\"," +
              "\"gender\" : \"MALE\"," +
              "\"dob\" : [ 2001, 1, 1 ]," +
              "\"assessmentDate\" : [ 2021, 1, 1, 0, 0 ]," +
              "\"currentOffence\" : {" +
              "    \"offenceCode\" : \"138\"," +
              "    \"offenceSubcode\" : \"00\"" +
              "}," +
              "\"dateOfFirstSanction\" : [ 2020, 1, 1 ]," +
              "\"totalOffences\" : 10," +
              "\"totalViolentOffences\" : 8," +
              "\"dateOfCurrentConviction\" : [ 2020, 12, 18 ]," +
              "\"hasAnySexualOffences\" : true," +
              "\"isCurrentSexualOffence\" : true," +
              "\"isCurrentOffenceVictimStranger\" : true," +
              "\"mostRecentSexualOffenceDate\" : [ 2020, 12, 11 ]," +
              "\"totalSexualOffencesInvolvingAnAdult\" : 5," +
              "\"totalSexualOffencesInvolvingAChild\" : 3," +
              "\"totalSexualOffencesInvolvingChildImages\" : 2," +
              "\"totalNonSexualOffences\" : 2," +
              "\"earliestReleaseDate\" : [ 2021, 11, 1 ]," +
              "\"hasCompletedInterview\" : true," +
              "\"dynamicScoringOffences\" : {" +
              "   \"committedOffenceUsingWeapon\" : true," +
              "   \"hasSuitableAccommodation\" : \"MISSING\"," +
              "   \"employment\": \"NOT_AVAILABLE_FOR_WORK\"," +
              "   \"currentRelationshipWithPartner\" : \"SIGNIFICANT_PROBLEMS\"," +
              "   \"evidenceOfDomesticViolence\" : true," +
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
              "}," +
              "\"avictim\" : true," +
              "\"aperpetrator\" : true" +
              "}" +
              "}",
            true, true
          )
        )
        .willReturn(
          WireMock.aResponse()
            .withStatus(200)
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(riskRsrPredictors)
        )
    )
  }
  val riskRsrPredictors =
    """{
        "algorithmVersion": 3,
        "type": "RSR",
        "scoreType": "STATIC",
        "rsrScore": {"level": "HIGH", "score": 11.34, "isValid": true},
        "ospcScore": {"level": "NOT_APPLICABLE", "score": 0, "isValid" : false},
        "ospiScore": {"level": "NOT_APPLICABLE", "score": 0, "isValid" : false}
        }
    """.trimIndent()
}