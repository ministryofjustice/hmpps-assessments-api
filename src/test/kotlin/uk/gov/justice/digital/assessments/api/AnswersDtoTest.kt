package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AnswersDtoTest {
  @Test
  fun `deserializes answers from a textual NodeType`() {
    val testAnswers = """
      [
        "TEST_ANSWER"
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    assertThat(deserialized.answers.size).isEqualTo(1)
    assertThat(deserialized.answers.first().items.size).isEqualTo(1)
    assertThat(deserialized.answers.first().items.first()).isEqualTo("TEST_ANSWER")
  }

  @Test
  fun `deserializes answers from an array NodeType`() {
    val testAnswers = """
      [
        ["TEST_ANSWER"]
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    assertThat(deserialized.answers.size).isEqualTo(1)
    assertThat(deserialized.answers.first().items.size).isEqualTo(1)
    assertThat(deserialized.answers.first().items.first()).isEqualTo("TEST_ANSWER")
  }

  @Test
  fun `filters out empty answers from a textual NodeType`() {
    val testAnswers = """
      [
        ""
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    assertThat(deserialized.answers.size).isEqualTo(1)
    assertThat(deserialized.answers.first().items.size).isEqualTo(0)
  }

  @Test
  fun `filters out empty answers from an array NodeType`() {
    val testAnswers = """
      [
        [""]
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    assertThat(deserialized.answers.size).isEqualTo(1)
    assertThat(deserialized.answers.first().items.size).isEqualTo(0)
  }
}
