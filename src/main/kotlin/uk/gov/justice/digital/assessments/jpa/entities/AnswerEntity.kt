package uk.gov.justice.digital.assessments.jpa.entities

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.io.Serializable

object AnswerDeserializer : StdDeserializer<Answer>(Answer::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Answer {
    val item = ctxt.readValue(p, String::class.java)
    return Answer(item)
  }
}

@JsonDeserialize(using = AnswerDeserializer::class)
data class Answer(
  @JsonValue
  val item: String
) : Serializable {

  override fun equals(other: Any?): Boolean {
    if (other is String)
      return item == other
    if (other is Answer)
      return item == other.item

    return super.equals(other)
  }

  override fun toString(): String {
    return item
  }
}

data class AnswerEntity(
  var answers: Collection<Answer> = emptyList()
) : Serializable {
  constructor(answer: String) : this(listOf(Answer(answer)))
  constructor(answers: List<String>) : this(answers.map { a -> Answer(a) })
}
