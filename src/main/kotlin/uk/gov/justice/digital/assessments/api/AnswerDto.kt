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

@JsonSerialize(using = AnswerDtoSerializer::class)
data class AnswerDto(
  val items: Collection<String>
)

object AnswerDtoSerializer : StdSerializer<AnswerDto>(AnswerDto::class.java) {
  override fun serialize(value: AnswerDto?, gen: JsonGenerator?, provider: SerializerProvider?) {
    if (value == null)
      return

    with(value.items) {
      gen?.writeStartArray()
      forEach { gen?.writeString(it) }
      gen?.writeEndArray()
    }
  }
}

