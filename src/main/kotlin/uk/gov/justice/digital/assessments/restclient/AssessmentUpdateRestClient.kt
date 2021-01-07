package uk.gov.justice.digital.assessments.restclient

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto

@Component
class AssessmentUpdateRestClient {
    @Autowired
    @Qualifier("assessmentUpdateWebClient")
    internal lateinit var webClient: WebClient

    fun createOasysOffender(crn: String): Long? {

        return webClient.post()
            .uri("/offenders")
            .bodyValue(CreateOffenderDto(crn = crn) )
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(CreateOffenderResponseDto::class.java)
            .block().oasysOffenderId
    }
}