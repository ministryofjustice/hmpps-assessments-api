package uk.gov.justice.digital.assessments.restclient

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import uk.gov.justice.digital.assessments.restclient.courtcaseapi.CourtCase

@Component
class CourtCaseRestClient {
    @Autowired
    @Qualifier("courtCaseWebClient")
    internal lateinit var webClient: WebClient
    @Value("\${court-case-api.case-path-template}")
    internal lateinit var casePathTemplate: String

    fun getCourtCase(courtCode: String?, caseNumber: String?): CourtCase? {
        val path = String.format(casePathTemplate, courtCode, caseNumber)
        return webClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CourtCase::class.java)
                .block()
    }

    fun getCourtCaseJson(courtCode: String, caseNumber: String): String? {
        val path = String.format(casePathTemplate, courtCode, caseNumber)
        return webClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()
    }
}