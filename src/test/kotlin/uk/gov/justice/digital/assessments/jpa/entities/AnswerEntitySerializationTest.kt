package uk.gov.justice.digital.assessments.jpa.entities

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

@DisplayName("Answer Entity Serialization Tests")
class AnswerEntitySerializationTest {
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

    fun writeAnswers(ae: AnswerEntity): String =
      om.writeValueAsString(ae)
  }

  @Nested
  @DisplayName("Serialize")
  inner class Serialization {
    @Test
    fun `single value answer`() {
      val ae = AnswerEntity("Fruit")

      val asString = writeAnswers(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\"]}")
    }

    @Test
    fun `multi-value answer`() {
      val ae = AnswerEntity(listOf("Fruit", "Vegetables"))

      val asString = writeAnswers(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\",\"Vegetables\"]}")
    }


    @Test
    fun `compound multi-value answer`() {
      val ae = AnswerEntity(Answer("Fruit"), Answer("Potatoes", "Carrots", "Onions"))

      val asString = writeAnswers(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\",[\"Potatoes\",\"Carrots\",\"Onions\"]]}")
    }
  }

  @Nested
  @DisplayName("Deserialize")
  inner class Deserialization {
    @Test
    fun `single value array answer`() {
      val ae = readAnswers("{\"answers\":[\"Fruit\"]}")

      assertThat(ae).isEqualTo(AnswerEntity("Fruit"))
    }

    @Test
    fun `single value answer`() {
      val ae = readAnswers("{\"answers\":\"Fruit\"}")

      assertThat(ae).isEqualTo(AnswerEntity("Fruit"))
    }

    @Test
    fun `multi-value answer`() {
      val ae = readAnswers("{\"answers\":[\"Animal\",\"Vegetable\",\"Mineral\"]}")

      assertThat(ae).isEqualTo(AnswerEntity(listOf("Animal","Vegetable","Mineral")))
    }

    @Test
    fun `compound multi-value answer`() {
      val ae = readAnswers("{\"answers\":[\"Fruit\",[\"Potatoes\",\"Carrots\",\"Onions\"]]}")
      val expected = AnswerEntity(Answer("Fruit"), Answer("Potatoes", "Carrots", "Onions"))

      assertThat(ae).isEqualTo(expected)
    }
  }
}