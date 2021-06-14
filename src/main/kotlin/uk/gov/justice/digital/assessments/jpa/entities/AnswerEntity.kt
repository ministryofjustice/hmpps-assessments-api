package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable

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
}

data class AnswerEntity(
  var answers: Collection<Answer> = emptyList()
) : Serializable {
  companion object {
    fun from(answer: String) = AnswerEntity(listOf(Answer(listOf(answer))))
    fun from(answers: List<String>) = AnswerEntity(answers.map { Answer(it) })
  }
}
