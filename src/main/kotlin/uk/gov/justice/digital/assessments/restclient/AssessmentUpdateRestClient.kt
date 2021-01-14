package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderDto
import uk.gov.justice.digital.assessments.restclient.assessmentupdateapi.CreateOffenderResponseDto

@Component
class AssessmentUpdateRestClient {
    @Autowired
    @Qualifier("assessmentUpdateWebClient")
    internal lateinit var webClient: AuthenticatingRestClient

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    fun createOasysOffender(crn: String, user: String ="STUARTWTHILAM", area: String = "WWS", deliusEvent: Int=123456): Long? {
        log.info("Creating offender in OASys for crn: $crn, area: $area, user: $user, delius event: $deliusEvent")
        return webClient
            .post("/offenders", CreateOffenderDto(crn, area, user,deliusEvent))
            .retrieve()
            .bodyToMono(CreateOffenderResponseDto::class.java)
            .block()?.oasysOffenderId
    }
}