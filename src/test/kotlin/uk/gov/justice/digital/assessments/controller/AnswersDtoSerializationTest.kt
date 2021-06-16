package uk.gov.justice.digital.assessments.controller

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto

@DisplayName("AnswersDto Serialization Tests")
class AnswersDtoSerializationTest {
  companion object {
    lateinit var om: ObjectMapper

    @BeforeAll
    @JvmStatic
    fun buildObjectMapper() {
      om = ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
        .registerModules(Jdk8Module(), JavaTimeModule(), KotlinModule())
    }

    fun writeAnswers(ae: AnswersDto): String =
      om.writeValueAsString(ae)
  }

  @Test
  fun `single value answer`() {
    val dto = AnswersDto(listOf(AnswerDto(listOf("Fruit"))))

    assertJsonIs(
      dto,
      "[\"Fruit\"]"
    )
  }

  @Test
  fun `multi-value answer`() {
    val dto = AnswersDto(listOf(AnswerDto(listOf("Fruit", "Vegetables"))))

    assertJsonIs(
      dto,
      "[\"Fruit\",\"Vegetables\"]"
    )
  }

  @Nested
  @DisplayName("Supporting table rows - multivalue answer structure is preserved")
  inner class TableRow {
    @Test
    fun `two single value answers`() {
      val dto = AnswersDto(
        listOf(
          AnswerDto(listOf("Fruit")),
          AnswerDto(listOf("Vegetables"))
        )
      )

      assertJsonIs(
        dto,
        "[[\"Fruit\"],[\"Vegetables\"]]"
      )
    }

    @Test
    fun `compound multi-value answer 1`() {
      val dto = AnswersDto(
        listOf(
          AnswerDto(listOf("Fruit")),
          AnswerDto(listOf("Potatoes", "Carrots", "Onions"))
        )
      )

      assertJsonIs(
        dto,
        "[[\"Fruit\"],[\"Potatoes\",\"Carrots\",\"Onions\"]]"
      )
    }

    @Test
    fun `compound multi-value answer 2`() {
      val dto = AnswersDto(
        listOf(
          AnswerDto(listOf("Potatoes", "Carrots", "Onions")),
          AnswerDto(listOf("Fruit"))
        )
      )

      assertJsonIs(
        dto,
        "[[\"Potatoes\",\"Carrots\",\"Onions\"],[\"Fruit\"]]"
      )
    }

    @Test
    fun `compound multi-value answer 3`() {
      val dto = AnswersDto(
        listOf(
          AnswerDto(listOf("Bananas")),
          AnswerDto(listOf("Potatoes", "Carrots", "Onions")),
          AnswerDto(listOf("Fruit"))
        )
      )

      assertJsonIs(
        dto,
        "[[\"Bananas\"],[\"Potatoes\",\"Carrots\",\"Onions\"],[\"Fruit\"]]"
      )
    }

    @Test
    fun `compound multi-value answer 4`() {
      val dto = AnswersDto(
        listOf(
          AnswerDto(listOf("Bananas", "Apples")),
          AnswerDto(listOf("Potatoes", "Carrots", "Onions"))
        )
      )

      assertJsonIs(
        dto,
        "[[\"Bananas\",\"Apples\"],[\"Potatoes\",\"Carrots\",\"Onions\"]]"
      )
    }
  }

  private fun assertJsonIs(dto: AnswersDto, expected: String) {
    val jsonString = writeAnswers(dto)

    assertThat(jsonString).isEqualTo(expected)
  }
}
