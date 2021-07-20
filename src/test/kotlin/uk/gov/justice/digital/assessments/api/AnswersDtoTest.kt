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
    with (deserialized.answers) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(listOf("TEST_ANSWER"))
    }
  }

  @Test
  fun `deserializes answers from an array NodeType`() {
    val testAnswers = """
      [
        ["TEST_ANSWER"]
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    with (deserialized.answers) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(listOf("TEST_ANSWER"))
    }
  }

  @Test
  fun `handles and empty list`() {
    val testAnswers = """
      []
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    assertThat(deserialized.answers.size).isEqualTo(0)
  }

  @Test
  fun `filters out empty answers from a textual NodeType`() {
    val testAnswers = """
      [
        ""
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    with (deserialized.answers) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(emptyList<AnswerDto>())
    }
  }

  @Test
  fun `filters out empty answers from an array NodeType`() {
    val testAnswers = """
      [
        [""]
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    with (deserialized.answers) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(emptyList<AnswerDto>())
    }
  }

  @Test
  fun `filters out empty answers when passed an empty list`() {
    val testAnswers = """
      [
        []
      ]
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, AnswersDto::class.java)
    with (deserialized.answers) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(emptyList<AnswerDto>())
    }
  }
}
