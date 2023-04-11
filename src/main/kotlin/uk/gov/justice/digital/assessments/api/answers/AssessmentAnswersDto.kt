package uk.gov.justice.digital.assessments.api.answers

import java.util.UUID

data class AssessmentAnswersDto(
  val assessmentUuid: UUID,
  // Question Code -> List Of Answer Dtos
  val answers: Map<String, Collection<AnswerDto>>,
)
