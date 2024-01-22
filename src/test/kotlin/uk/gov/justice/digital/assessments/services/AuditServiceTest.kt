package uk.gov.justice.digital.assessments.services

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import uk.gov.justice.digital.assessments.restclient.AuditClient
import uk.gov.justice.digital.assessments.restclient.AuditDetail
import uk.gov.justice.digital.assessments.restclient.AuditType
import uk.gov.justice.digital.assessments.restclient.AuditableEvent
import uk.gov.justice.digital.assessments.services.exceptions.AuditFailureException
import uk.gov.justice.digital.assessments.utils.RequestData
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

@ExtendWith(MockKExtension::class)
@DisplayName("Audit Service Tests")
class AuditServiceTest {

  private val auditClient: AuditClient = mockk()
  private val objectMapper = ObjectMapper()
  private val clock: Clock = Clock.fixed(Instant.ofEpochSecond(1635419271), ZoneId.systemDefault())
  private val auditService: AuditService = AuditService("hmpps-assessments-api", objectMapper, auditClient, clock)
  private val serviceName = "hmpps-assessments-api"
  private val crn = "X123456C"
  private val assessmentUUID = UUID.randomUUID()
  private val episodeUUID = UUID.randomUUID()
  private val authorUUID = UUID.randomUUID()
  private val auditType = AuditType.ARN_ASSESSMENT_CREATED
  private val actualAuditEvent = slot<AuditableEvent>()
  private val authorEntity = AuthorEntity(userId = "user id", userName = "user name", authorUuid = authorUUID)

  @Test
  fun `submits audit event`() {
    mockkObject(RequestData) {
      every { RequestData.getUserName() } returns "user name"

      val expectedAuditEvent = AuditableEvent(
        what = auditType.name,
        who = RequestData.getUserName(),
        service = serviceName,
        `when` = Instant.now(clock),
        details = objectMapper.writeValueAsString(
          AuditDetail(
            crn = crn,
            assessmentUuid = assessmentUUID,
            episodeUuid = episodeUUID,
            author = authorEntity,
            null,
          ),
        ),
      )

      justRun { auditClient.sendEvent(capture(actualAuditEvent)) }
      auditService.createAuditEvent(auditType, assessmentUUID, episodeUUID, crn, authorEntity, null)
      assertThat(expectedAuditEvent).isEqualTo(actualAuditEvent.captured)
    }
  }

  @Test
  fun `submits audit event with additional detail`() {
    mockkObject(RequestData) {
      every { RequestData.getUserName() } returns "user name"

      val expectedAuditEvent = AuditableEvent(
        what = auditType.name,
        who = RequestData.getUserName(),
        service = serviceName,
        `when` = Instant.now(clock),
        details = objectMapper.writeValueAsString(
          AuditDetail(
            crn = crn,
            assessmentUuid = assessmentUUID,
            episodeUuid = episodeUUID,
            author = authorEntity,
            additionalDetails = mapOf("allocatedFrom" to "user 1"),
          ),
        ),
      )

      justRun { auditClient.sendEvent(capture(actualAuditEvent)) }
      auditService.createAuditEvent(auditType, assessmentUUID, episodeUUID, crn, authorEntity, mapOf("allocatedFrom" to "user 1"))
      assertThat(actualAuditEvent.captured).isEqualTo(expectedAuditEvent)
    }
  }

  @Test
  fun `submit audit event does not fail on error`() {
    mockkObject(RequestData) {
      every { RequestData.getUserName() } returns "user name"

      every { auditClient.sendEvent(capture(actualAuditEvent)) } throws AuditFailureException("An error occurred")
      assertDoesNotThrow {
        auditService.createAuditEvent(auditType, assessmentUUID, episodeUUID, crn, authorEntity, null)
      }
    }
  }
}
