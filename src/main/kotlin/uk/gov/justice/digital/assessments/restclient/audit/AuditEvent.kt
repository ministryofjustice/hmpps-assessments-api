package uk.gov.justice.digital.assessments.restclient.audit

import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.time.Instant
import java.util.UUID

data class AuditEvent(
  val what: String,
  val `when`: Instant,
  val who: String?,
  val service: String?,
  val details: String?
)

data class AuditDetail(
  val crn: String?,
  val assessmentUuid: UUID,
  val episodeUuid: UUID?,
  val author: AuthorEntity?,
  val additionalDetails: Any?
)

enum class AuditType {
  ARN_ASSESSMENT_CREATED,
  ARN_ASSESSMENT_UPDATED,
  ARN_ASSESSMENT_CLOSED,
  ARN_ASSESSMENT_REASSIGNED,
  ARN_ASSESSMENT_COMPLETED
}
