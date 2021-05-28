package uk.gov.justice.digital.assessments.jpa.entities

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
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
  }

  @Nested
  @DisplayName("Serialize")
  inner class Serialization {
    @Test
    fun `single value answer`() {
      val ae = AnswerEntity("Fruit")

      val asString = om.writeValueAsString(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\"]}")
    }

    @Test
    fun `multi-value answer`() {
      val ae = AnswerEntity(listOf("Fruit", "Vegetables"))

      val asString = om.writeValueAsString(ae)

      assertThat(asString).isEqualTo("{\"answers\":[\"Fruit\",\"Vegetables\"]}")
    }
  }

  @Nested
  @DisplayName("Deserialize")
  inner class Deserialization {
    @Test
    fun `single value array answer`() {
      val ae = om.readValue("{\"answers\":[\"Fruit\"]}", AnswerEntity::class.java)

      assertThat(ae).isEqualTo(AnswerEntity("Fruit"))
    }

    @Test
    fun `single value answer`() {
      val ae = om.readValue("{\"answers\":\"Fruit\"}", AnswerEntity::class.java)

      assertThat(ae).isEqualTo(AnswerEntity("Fruit"))
    }

    @Test
    fun `multi-value answer`() {
      val ae = om.readValue("{\"answers\":[\"Animal\",\"Vegetable\",\"Mineral\"]}", AnswerEntity::class.java)

      assertThat(ae).isEqualTo(AnswerEntity(listOf("Animal","Vegetable","Mineral")))
    }
  }
}