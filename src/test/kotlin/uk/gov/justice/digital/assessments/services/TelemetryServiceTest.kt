package uk.gov.justice.digital.assessments.services

import com.microsoft.applicationinsights.TelemetryClient
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.util.UUID

@DisplayName("Telemetry Service Tests")
@ExtendWith(MockKExtension::class)
class TelemetryServiceTest {

  val telemetryClient: TelemetryClient = mockk<TelemetryClient>()
  val telemetryService = TelemetryService(telemetryClient)
  val assessmentUuid = UUID.randomUUID()
  val episodeUuid = UUID.randomUUID()
  val event = TelemetryEventType.ASSESSMENT_CREATED
  val author = AuthorEntity(userName = "USER1", userId = "User_Id")
  val crn = "DX0123456C"
  val assessmentType = AssessmentType.UPW

  @Test
  fun `logs custom event to application insights`() {
    val expectedProperties = mapOf(
      "author" to author.userName,
      "crn" to crn,
      "assessmentUUID" to assessmentUuid.toString(),
      "episodeUUID" to episodeUuid.toString(),
      "assessmentType" to assessmentType.name,
    )

    justRun { telemetryClient.trackEvent("arnAssessmentCreated", any(), any()) }
    telemetryService.trackAssessmentEvent(event, crn, author, assessmentUuid, episodeUuid, assessmentType)
    verify(exactly = 1) { telemetryClient.trackEvent("arnAssessmentCreated", expectedProperties, any()) }
  }
}
