package uk.gov.justice.digital.assessments.services

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.util.UUID

@Service
class TelemetryService(
  private val telemetryClient: TelemetryClient
) {

  fun trackAssessmentEvent(event: TelemetryEventType, crn: String, author: AuthorEntity, assessmentUuid: UUID, episodeUuid: UUID) {
    val properties = mapOf(
      "author" to author.userName,
      "crn" to crn,
      "assessmentUUID" to assessmentUuid.toString(),
      "episodeUUID" to episodeUuid.toString()
    )
    telemetryClient.trackEvent(event.eventName, properties, emptyMap())
  }
}

enum class TelemetryEventType(val eventName: String) {
  ASSESSMENT_CREATED("arnAssessmentCreated"),
  ASSESSMENT_REALLOCATED("aarnAssessmentReallocated"),
  ASSESSMENT_COMPLETE("arnAssessmentCompleted")
}
