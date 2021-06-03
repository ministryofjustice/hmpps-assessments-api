package uk.gov.justice.digital.assessments.api

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import java.util.UUID

class UpdateAssessmentEpisodeDto(
  @Schema(description = "Answers associated with this episode")
  val answers: Map<UUID, Collection<Answer>>
) {
  fun coalesce(): UpdateAssessmentEpisodeDto {
    val coalesced = mutableMapOf<UUID, Collection<Answer>>()

    answers.forEach { uuid, collection ->
      coalesced.put(uuid, coalesce(collection))
    }

    return UpdateAssessmentEpisodeDto(coalesced)
  }

  private fun coalesce(collection: Collection<Answer>): Collection<Answer> {
    if (collection.size > 1) {
      val flattened = collection.map { it.items }.flatten()
      if (flattened.size == collection.size)
        return listOf(Answer(flattened))
    }
    return collection
  }
}
