package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.lang.IllegalStateException

object AnswerDtoSerializer : StdSerializer<AnswerDto>(AnswerDto::class.java) {
  override fun serialize(value: AnswerDto?, gen: JsonGenerator?, provider: SerializerProvider?) {
    if (value == null)
      return

    with (value.items) {
      if(size == 1) {
        gen?.writeString(first())
      } else {
        gen?.writeStartArray()
        forEach { gen?.writeString(it) }
        gen?.writeEndArray()
      }
    }
  }
}

object AnswerDtoDeserializer : StdDeserializer<AnswerDto>(AnswerDto::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): AnswerDto {
    val node = ctxt.readTree(p)

    if (node.isTextual) {
      val item = ctxt.readValue(p, String::class.java)
      return AnswerDto(listOf(item))
    }
    if (node.isArray) {
      val items = node.elements().asSequence().map { it.asText() }.toList()
      return AnswerDto(items)
    }

    throw IllegalStateException("Expected a string or array of strings to deserialise an Answer, but type was ${node.nodeType}")
  }
}

@JsonSerialize(using = AnswerDtoSerializer::class)
@JsonDeserialize(using = AnswerDtoDeserializer::class)
data class AnswerDto(
  val items: Collection<String>
)

