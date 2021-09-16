package uk.gov.justice.digital.assessments.testutils

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.HttpHeader
import com.github.tomakehurst.wiremock.http.HttpHeaders

class CourtCaseMockServer : WireMockServer(9002) {
  fun stubCourtCase() {
    stubFor(
      WireMock.get(WireMock.urlEqualTo("/court/SHF06/case/668911253"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(courtCaseJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/court/courtCode/case/caseNumber"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withBody(courtCaseJson)
        )
    )

    stubFor(
      WireMock.get(WireMock.urlEqualTo("/court/notfound/case/668911253"))
        .willReturn(
          WireMock.aResponse()
            .withHeaders(HttpHeaders(HttpHeader("Content-Type", "application/json")))
            .withStatus(404)
            .withBody("{\"status\":\"404\",\"developerMessage\":\"court not found\"}")
        )
    )
  }

  companion object {
    val courtCaseJson =
      """{
  "caseId": "951609",
  "caseNo": "668911253",
  "courtCode": "SHF",
  "courtRoom": "06",
  "sessionStartTime": "2020-12-07T09:30:00",
  "listNo": "2nd",
  "pnc": "A/1234560BA",
  "crn": "DX12340A",
  "probationStatus": "Current",
  "session": "MORNING",
  "previouslyKnownTerminationDate": null,
  "suspendedSentenceOrder": false,
  "breach": false,
  "offences": [
    {
      "offenceTitle": "Attempt theft from the person of another",
      "offenceSummary": "On 05/09/2016 at Glasgow attempted to steal GAMES CONSOLES to the value of 750.00, belonging to Clemons Barron.",
      "act": "Contrary to section 1(1) of the Criminal Attempts Act 1981."
    },
    {
      "offenceTitle": "Theft from the person of another",
      "offenceSummary": "On 24/03/2016 at Leeds stole PLAYSTATION 4 to the value of 300.00, belonging to Hull Obrien.",
      "act": "Contrary to section 1(1) and 7 of the Theft Act 1968."
    },
    {
      "offenceTitle": "Attempt theft from the person of another",
      "offenceSummary": "On 05/09/2016 at Aberdeen attempted to steal FLAT SCREEN TV to the value of 750.00, belonging to Howard Ashley.",
      "act": "Contrary to section 1(1) of the Criminal Attempts Act 1981."
    },
    {
      "offenceTitle": "Attempt theft from the person of another",
      "offenceSummary": "On 05/09/2016 at Glasgow attempted to steal GAMES CONSOLES to the value of 750.00, belonging to Conner Powell.",
      "act": "Contrary to section 1(1) of the Criminal Attempts Act 1981."
    }
  ],
  "defendantSex": "F",
  "defendantName": "John Smith",
  "defendantAddress": {
    "line1": "38",
    "line2": "Clarendon Road",
    "postcode": "ad21 5dr",
    "line3": "Glasgow",
    "line4": null,
    "line5": null
  },
  "defendantDob": "1979-08-18",
  "nationality1": "British"
}
      """.trimIndent()
  }
}
