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
import uk.gov.justice.digital.assessments.jpa.entities.AnswerEntity

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

    fun readAnswers(jsonText: String): AnswerEntity =
      om.readValue(jsonText, AnswerEntity::class.java)

    fun writeAnswers(ae: AnswersDto): String =
      om.writeValueAsString(ae)
  }

  @Nested
  @DisplayName("Serialize")
  inner class Serialization {
    @Test
    fun `single value answer`() {
      val ae = AnswersDto(listOf(AnswerDto(listOf("Fruit"))))

      val asString = writeAnswers(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\"]}")
    }

    @Test
    fun `multi-value answer`() {
      val ae = AnswersDto(listOf(AnswerDto(listOf("Fruit", "Vegetables"))))

      val asString = writeAnswers(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\",\"Vegetables\"]}")
    }

    @Test
    fun `compound multi-value answer`() {
      val ae = AnswersDto(listOf(
        AnswerDto(listOf("Fruit")),
        AnswerDto(listOf("Potatoes", "Carrots", "Onions"))))

      val asString = writeAnswers(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\",[\"Potatoes\",\"Carrots\",\"Onions\"]]}")
    }
  }
}