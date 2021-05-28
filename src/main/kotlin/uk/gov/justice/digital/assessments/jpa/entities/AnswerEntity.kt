package uk.gov.justice.digital.assessments.jpa.entities

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.io.Serializable
import java.lang.IllegalStateException

object AnswerSerializer : StdSerializer<Answer>(Answer::class.java) {
  override fun serialize(value: Answer?, gen: JsonGenerator?, provider: SerializerProvider?) {
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

object AnswerDeserializer : StdDeserializer<Answer>(Answer::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Answer {
    val node = ctxt.readTree(p)

    if (node.isTextual) {
      val item = ctxt.readValue(p, String::class.java)
      return Answer(item)
    }
    if (node.isArray) {
      val items = node.elements().asSequence().map { it.asText() }.toList()
      return Answer(items)
    }

    throw IllegalStateException("Expected a string or array of strings to deserialise an Answer, but type was ${node.nodeType}")
  }
}

@JsonSerialize(using = AnswerSerializer::class)
@JsonDeserialize(using = AnswerDeserializer::class)
data class Answer(
  val items: Collection<String>
) : Serializable {
  constructor(vararg items: String) : this(listOf(*items))

  override fun equals(other: Any?): Boolean {
    if (other is String)
      return (items.size == 1) && (items.first() == other)
    if (other is Answer)
      return items == other.items

    return super.equals(other)
  }

  // TODO this needs to change!
  override fun toString(): String {
    return items.first()
  }
}

data class AnswerEntity(
  var answers: Collection<Answer> = emptyList()
) : Serializable {
  constructor(answer: String) : this(listOf(Answer(answer)))
  constructor(answers: List<String>) : this(answers.map { a -> Answer(a) })
  constructor(vararg answers: Answer) : this(listOf(*answers))
}
