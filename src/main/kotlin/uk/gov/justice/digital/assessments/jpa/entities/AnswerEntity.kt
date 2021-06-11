package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable

data class AnswerEntity(
  var answers: Collection<String> = emptyList()
) : Serializable {
  constructor(answer: String) : this(listOf(answer))
}
