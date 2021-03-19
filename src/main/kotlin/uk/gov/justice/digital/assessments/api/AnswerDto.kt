package uk.gov.justice.digital.assessments.api

import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import java.util.UUID

class AnswerDto {
  companion object {
    fun from(answers: Map<UUID, AnswerEntity>?): Map<UUID, Collection<String>>? {
      return answers?.mapValues { it.value.answers }
    }
  }
}
