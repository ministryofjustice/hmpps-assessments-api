package uk.gov.justice.digital.assessments.restclient

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.restclient.audit.AuditDetail
import uk.gov.justice.digital.assessments.restclient.audit.AuditEvent
import uk.gov.justice.digital.assessments.restclient.audit.AuditType
import uk.gov.justice.digital.assessments.services.exceptions.AuditFailureException
import uk.gov.justice.digital.assessments.testutils.IntegrationTest
import java.time.Instant
import java.util.UUID

class AuditClientTest : IntegrationTest() {
  @Autowired
  internal lateinit var auditClient: AuditRestClient

  val auditEvent = AuditEvent(
    what = AuditType.ARN_ASSESSMENT_CREATED.name,
    who = "user@justice.gov.uk",
    service = "hmpps-assessments-api",
    `when` = Instant.now(),
    details = AuditDetail(
      "X123456C",
      UUID.randomUUID(),
      UUID.randomUUID(),
      AuthorEntity(
        1,
        UUID.randomUUID(),
        "user id",
        "AUser",
        "DELIUS",
        "Full Name"
      ),
      null
    )
  )

  @Test
  fun `submit new audit event`() {

    assertDoesNotThrow {
      auditClient.createAuditEvent(auditEvent)
    }
  }

  @Test
  fun `throws exception when submit new audit event fails`() {

    assertThrows<AuditFailureException> {
      auditClient.createAuditEvent(auditEvent.copy(who = "error-user@justice.gov.uk"))
    }
  }
}
