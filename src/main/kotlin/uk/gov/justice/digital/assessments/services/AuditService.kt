package uk.gov.justice.digital.assessments.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.restclient.AuditRestClient
import uk.gov.justice.digital.assessments.restclient.audit.AuditDetail
import uk.gov.justice.digital.assessments.restclient.audit.AuditEvent
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.services.exceptions.AuditFailureException
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class AuditService(
  private val auditClient: AuditRestClient,
  @Value("\${spring.application.name}")
  private val serviceName: String,
  private val mapper: ObjectMapper,
  private val clock: Clock = Clock.systemUTC()
) {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun createAuditEvent(auditType: AuditType, assessmentUUID: UUID, episodeUUID: UUID?, crn: String?, author: AuthorEntity?, additionalDetails: Any? = null) {

    val auditEvent = AuditEvent(
      what = auditType.name,
      who = RequestData.getUserName(),
      service = serviceName,
      `when` = Instant.now(clock),
      details = AuditDetail(
        crn = crn,
        assessmentUuid = assessmentUUID,
        episodeUuid = episodeUUID,
        author = author,
        if (additionalDetails != null) mapper.writeValueAsString(additionalDetails) else null
      )
    )
    // Initially log audit failures as errors,
    // so they can be alerted on but do not prevent the user completing the transaction
    try {
      auditClient.createAuditEvent(auditEvent)
    } catch (e: AuditFailureException) {
      log.error(e.message)
    }
  }
}
