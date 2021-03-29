package uk.gov.justice.digital.assessments.api

import java.util.UUID

data class AssessmentAnswersDto(
  val assessmentUuid: UUID,
  // Question Code -> List Of Answer Schema Dto
  val answers: Map<String, Collection<AnswerSchemaDto>>
)
