package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.util.UUID

data class AnswerEntity(
  var answers: Collection<String> = emptyList()
) : Serializable {
  constructor(answer: String) : this(listOf(answer)) { }
}
