package uk.gov.justice.digital.assessments.jpa.entities

import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.gov.justice.digital.assessments.api.AnswerDto

@ExtendWith(MockKExtension::class)
@DisplayName("Answer Entity Tests")
class AnswerEntitiesTest {
  @Nested
  @DisplayName("Answer")
  inner class AnswerTest {
    @Test
    fun `should construct when passed a list of strings`() {
      val answerDto = AnswerDto(listOf("FOO", "BAR"))
      val answer = Answer(answerDto.items)

      with(answer.items) {
        assertThat(size).isEqualTo(2)
        assertThat(first()).isEqualTo("FOO")
      }
    }

    @Test
    fun `should construct when passed an empty list`() {
      val answerDto = AnswerDto(emptyList())
      val answer = Answer(answerDto.items)

      assertThat(answer.items.size).isEqualTo(0)
    }
  }

  @Nested
  @DisplayName("Answer")
  inner class AnswerEntityTest {
    @Test
    fun `should construct from a string`() {
      val answerEntity = AnswerEntity.from("FOO")

      with(answerEntity.answers) {
        assertThat(size).isEqualTo(1)
        assertThat(first().items).isEqualTo(listOf("FOO"))
      }
    }

    @Test
    fun `should construct from an empty list`() {
      val answerEntity = AnswerEntity.from(emptyList())

      assertThat(answerEntity.answers.size).isEqualTo(0)
    }

    @Test
    fun `should construct from a list of strings`() {
      val answerEntity = AnswerEntity.from(listOf("FOO", "BAR"))

      with(answerEntity.answers) {
        assertThat(size).isEqualTo(2)
        assertThat(first().items).isEqualTo(listOf("FOO"))
      }
    }
  }
}
