package uk.gov.justice.digital.assessments.api

import java.util.*

data class FilteredReferenceDataRequest(
  val assessmentUuid: UUID,
  val episodeUuid: UUID,
  val fieldName: UUID,
  val parentList: Map<UUID, String>?
)
