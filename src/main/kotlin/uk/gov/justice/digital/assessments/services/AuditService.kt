package uk.gov.justice.digital.assessments.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.restclient.AuditClient
import uk.gov.justice.digital.assessments.restclient.AuditDetail
import uk.gov.justice.digital.assessments.restclient.AuditType
import uk.gov.justice.digital.assessments.restclient.AuditableEvent
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.Clock
import java.time.Instant
import java.util.UUID

@Service
class AuditService(
  @Value("\${spring.application.name}")
  private val serviceName: String,
  private val objectMapper: ObjectMapper,
  private val auditClient: AuditClient,
  private val clock: Clock = Clock.systemUTC(),
) {

  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun createAuditEvent(auditType: AuditType, assessmentUUID: UUID, episodeUUID: UUID?, crn: String?, author: AuthorEntity?, additionalDetails: Map<String, Any>? = null) {
    val event = AuditableEvent(
      who = RequestData.getUserName(),
      what = auditType.name,
      `when` = Instant.now(clock),
      service = serviceName,
      details = AuditDetail(
        crn = crn,
        assessmentUuid = assessmentUUID,
        episodeUuid = episodeUUID,
        author = author,
        additionalDetails,
      ).toJson(),
    )

    try {
      auditClient.sendEvent(event)
    } catch (e: Exception) {
      log.error(e.message)
    }
  }

  private fun Any.toJson() = objectMapper.writeValueAsString(this)
}
