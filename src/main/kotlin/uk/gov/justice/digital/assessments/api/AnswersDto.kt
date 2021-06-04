package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import java.util.UUID

@JsonSerialize(using = AnswersDtoSerializer::class)
data class AnswersDto(
  var answers: Collection<AnswerDto> = emptyList()
) {
  companion object {
    @JvmStatic
    @JsonCreator
    fun fromJson(answers: Collection<AnswerDto>): AnswersDto {
      if (answers.size > 1) {
        val flattened = answers.map { it.items }.flatten()
        if (flattened.size == answers.size) {
          return AnswersDto(listOf(AnswerDto(flattened)))
        }
      }
      return AnswersDto(answers)
    }

    fun from(answers: Map<UUID, AnswerEntity>?): Map<UUID, AnswersDto>? {
      return answers?.mapValues {
        from(it.value)
      }
    }

    private fun from(ae: AnswerEntity): AnswersDto {
      return AnswersDto(from(ae.answers))
    }
    private fun from(answer: Collection<Answer>): Collection<AnswerDto> {
      return answer.map { AnswerDto(it.items) }
    }
  }
}

object AnswersDtoSerializer : StdSerializer<AnswersDto>(AnswersDto::class.java) {
  override fun serialize(value: AnswersDto?, gen: JsonGenerator?, provider: SerializerProvider?) {
    if (value == null)
      return

    with (value.answers) {
      gen?.writeStartObject()
      gen?.writeFieldName("answers")
      if(size == 1 && first().items.size != 1) {
        gen?.writeObject(first())
      } else {
        gen?.writeStartArray()
        forEach { gen?.writeObject(it) }
        gen?.writeEndArray()
      }
      gen?.writeEndObject()
    }
  }
}
