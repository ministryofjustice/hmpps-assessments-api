package uk.gov.justice.digital.assessments.services

import com.microsoft.applicationinsights.TelemetryClient
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AuthorEntity
import java.time.LocalDateTime
import java.util.UUID

@Service
class TelemetryService(
  private val telemetryClient: TelemetryClient,
) {

  fun trackAssessmentEvent(
    event: TelemetryEventType,
    crn: String?,
    author: AuthorEntity,
    assessmentUuid: UUID,
    episodeUuid: UUID,
    assessmentType: AssessmentType,
    additionalFields: Map<String, String>? = null,
  ) {
    val properties = mutableMapOf(
      "author" to author.userName,
      "crn" to crn,
      "assessmentUUID" to assessmentUuid.toString(),
      "episodeUUID" to episodeUuid.toString(),
      "assessmentType" to assessmentType.name,
    )
    if (additionalFields != null) {
      properties.putAll(additionalFields)
    }
    telemetryClient.trackEvent(event.eventName, properties, emptyMap())
  }

  fun trackAssessmentClonedEvent(
    crn: String?,
    author: AuthorEntity,
    assessmentUuid: UUID,
    episodeUuid: UUID,
    assessmentType: AssessmentType,
    clonedFromEpisodeUUID: UUID,
    clonedFromCompletedDate: LocalDateTime,
  ) {
    trackAssessmentEvent(
      TelemetryEventType.ASSESSMENT_CLONED,
      crn,
      author,
      assessmentUuid,
      episodeUuid,
      assessmentType,
      mapOf(
        "mostRecentEpisodeUUID" to clonedFromEpisodeUUID.toString(),
        "mostRecentEpisodeCompletedDate" to clonedFromCompletedDate.toString(),
      ),
    )
  }
}

enum class TelemetryEventType(val eventName: String) {
  ASSESSMENT_CREATED("arnAssessmentCreated"),
  ASSESSMENT_CLOSED("arnAssessmentClosed"),
  ASSESSMENT_REALLOCATED("arnAssessmentReallocated"),
  ASSESSMENT_COMPLETE("arnAssessmentCompleted"),
  ASSESSMENT_CLONED("arnAssessmentCloned"),
}
