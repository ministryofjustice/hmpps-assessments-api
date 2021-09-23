package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders
import java.util.UUID

class AssessRisksAndNeedsApiMockServer : WireMockServer(9007) {
  fun stubGetRSRPredictorsForOffenderAndOffences(
    final: Boolean,
    episodeUuid: UUID
  ) {
    stubFor(
      WireMock.post(WireMock.urlEqualTo("/risks/predictors/RSR?final=$final&source=ASSESSMENTS_API&sourceId=$episodeUuid"))
        .withRequestBody(
          WireMock.equalToJson(
            "{ " +
              "\"crn\": \"X1345\"," +
              "\"gender\" : \"MALE\"," +
              "\"dob\" : [ 2001, 1, 1 ]," +
              "\"assessmentDate\" : [ 2021, 1, 1, 0, 0 ]," +
              "\"currentOffence\" : {" +
              "    \"offenceCode\" : \"054\"," +
              "    \"offenceSubcode\" : \"09\"" +
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
              "   \"firearmPossession\" :null, " +
              "   \"offencesWithWeapon\" : null " +
              "   } " +
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

  fun stubGetRSRPredictorsForOffenderAndOffencesWithCurrentOffences(
    final: Boolean,
    episodeUuid: UUID,
    crn: String,
    offenceCode: String = "138",
    offenceSubCode: String = "00"
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
}
