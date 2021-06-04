package uk.gov.justice.digital.assessments.api
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
  fun toAnswers(): Collection<Answer> {
    return answers.map { Answer(it.items) }
  }

  companion object {
    fun from(answers: Map<UUID, AnswerEntity>?): Map<UUID, AnswersDto>? {
      return answers?.mapValues {
        from(it.value)
      }
    }
    private fun from(ae: AnswerEntity): AnswersDto {
      return from(ae.answers)
    }

    fun from(answer: Collection<Answer>): AnswersDto {
      return AnswersDto(answer.map { AnswerDto(it.items) })
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
      if(size == 1) {
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
