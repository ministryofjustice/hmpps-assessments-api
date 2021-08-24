package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import uk.gov.justice.digital.assessments.jpa.entities.Answer
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity
import java.lang.IllegalStateException
import java.util.UUID

@JsonSerialize(using = AnswersDtoSerializer::class)
@JsonDeserialize(using = AnswersDtoDeserializer::class)
data class AnswersDto(
  var answers: Collection<AnswerDto> = emptyList()
) {
  fun toAnswers(): Collection<Answer> {
    return answers.map { Answer(it.items) }
  }

  companion object {
    fun from(answers: Map<String, AnswerEntity>?): Map<String, AnswersDto>? {
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

    with(value.answers) {
      if (size == 1) {
        gen?.writeObject(first())
      } else {
        gen?.writeStartArray()
        forEach { gen?.writeObject(it) }
        gen?.writeEndArray()
      }
    }
  }
}

object AnswersDtoDeserializer : StdDeserializer<AnswersDto>(AnswersDto::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AnswersDto {
    val node = ctxt.readTree(p)

    if (node.isTextual) {
      val item = ctxt.readValue(p, String::class.java)
      val items = deserialiseTextualAnswer(item)
      return AnswersDto(listOf(AnswerDto(items)))
    }
    if (node.isArray) {
      val answers = node.elements().asSequence().map { deserialiseAnswerDto(it) }.toList()
      return AnswersDto(answers)
    }

    throw IllegalStateException("Expected a string or array of strings to deserialise an Answer, but type was ${node.nodeType}")
  }

  private fun deserialiseAnswerDto(node: JsonNode): AnswerDto {
    if (node.isTextual) {
      val item = node.asText()
      val items = deserialiseTextualAnswer(item)
      return AnswerDto(items)
    }
    if (node.isArray) {
      val items = node.elements().asSequence().map { it.asText() }.filter { it.isNotBlank() }.toList()
      return AnswerDto(items)
    }

    throw IllegalStateException("Expected a string or array of strings to deserialise an Answer, but type was ${node.nodeType}")
  }

  private fun deserialiseTextualAnswer(s: String): List<String> {
    return if (s.isNotBlank()) listOf(s) else emptyList()
  }
}
