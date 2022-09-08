package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import uk.gov.justice.digital.assessments.utils.offenderStubResource.OffenderStubDto

@Component
class AssessmentUpdateRestClient {
  @Autowired
  @Qualifier("assessmentUpdateWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  /*
  * Use this method only if you want to force Creating Offender Stub in Oasys
  * */
  fun createOasysOffenderStub(offenderStubDto: OffenderStubDto) {
    log.info("Creating offender stub in OASys for crn: ${offenderStubDto.crn}")
    val path = "/offender/stub"
    webClient
      .post(path, offenderStubDto)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError) {
        handle4xxError(
          it,
          HttpMethod.POST, path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .onStatus(HttpStatus::is5xxServerError) {
        handle5xxError(
          "Failed to create offender stub ${offenderStubDto.crn} in OASYs",
          HttpMethod.POST, path,
          ExternalService.ASSESSMENTS_UPDATE
        )
      }
      .bodyToMono(Long::class.java)
      .block()?.also {
        log.info("Created offender stub in OASys for crn: ${offenderStubDto.crn}")
      }
  }
}
