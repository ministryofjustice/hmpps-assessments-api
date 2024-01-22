package uk.gov.justice.digital.assessments.restclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.services.AuditService
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import java.time.Instant
import java.util.UUID

@Service
class AuditClient(
  private val hmppsQueueService: HmppsQueueService,
  private val objectMapper: ObjectMapper,
) {
  private val queueId = "audit"
  private val auditQueue by lazy {
    hmppsQueueService.findByQueueId(queueId) ?: throw RuntimeException("Queue with ID '$queueId' does not exist'")
  }
  private val sqsClient by lazy { auditQueue.sqsClient }
  private val queueUrl by lazy { auditQueue.queueUrl }

  fun sendEvent(event: AuditableEvent) {
    sqsClient.sendMessage {
      it.queueUrl(queueUrl)
        .messageBody(event.toJson())
        .build()
    }
      .get()
      .also { AuditService.log.info("Audit event ${event.what} for ${event.who} sent") }
  }

  private fun Any.toJson() = objectMapper.writeValueAsString(this)
}

data class AuditableEvent(
  val who: String,
  val what: String,
  val `when`: Instant = Instant.now(),
  val service: String,
  val details: String,
)

data class AuditDetail(
  val crn: String?,
  val assessmentUuid: UUID,
  val episodeUuid: UUID?,
  val author: AuthorEntity?,
  val additionalDetails: Any?,
)

enum class AuditType {
  ARN_ASSESSMENT_CREATED,
  ARN_ASSESSMENT_UPDATED,
  ARN_ASSESSMENT_CLOSED,
  ARN_ASSESSMENT_REASSIGNED,
  ARN_ASSESSMENT_COMPLETED,
  ARN_ASSESSMENT_CLONED,
}
