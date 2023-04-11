package uk.gov.justice.digital.assessments.restclient

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import uk.gov.justice.digital.assessments.restclient.assessrisksandneedsapi.RiskPredictorsDto
import uk.gov.justice.digital.assessments.restclient.audit.AuditEvent
import uk.gov.justice.digital.assessments.services.exceptions.AuditFailureException

@Component
class AuditRestClient {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @Autowired
  @Qualifier("auditWebClient")
  internal lateinit var webClient: AuthenticatingRestClient

  @Value("\${audit.base-url}")
  internal lateinit var auditPathTemplate: String

  fun createAuditEvent(
    auditEvent: AuditEvent,
  ) {
    AssessRisksAndNeedsApiRestClient.log.info("Submitting audit event ${auditEvent.what} for User ${auditEvent.who}")
    val path = "$auditPathTemplate/audit"
    webClient.post(path, auditEvent)
      .retrieve()
      .bodyToMono(RiskPredictorsDto::class.java)
      .onErrorResume {
        Mono.error(
          AuditFailureException(
            "Failed to Audit event ${auditEvent.what} " +
              "for User ${auditEvent.who} by user ${auditEvent.who} for reason ${it.message}",
          ),
        )
      }
      .block().also { log.info("Audited event ${auditEvent.what} for User ${auditEvent.who}") }
  }
}
