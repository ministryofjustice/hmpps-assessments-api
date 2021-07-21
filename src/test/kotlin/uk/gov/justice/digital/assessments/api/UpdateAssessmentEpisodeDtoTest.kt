package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class UpdateAssessmentEpisodeDtoTest {
  @Test
  fun `converts answers to AnswerDto`() {
    val testAnswers = """
     {
        "answers": {
          "00000000-0000-0000-0000-000000000000": [
            "FOO",
            "BAR"
          ]
        }
    }
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, UpdateAssessmentEpisodeDto::class.java)
    val transformed = deserialized.asAnswersDtos()
    val answersDto: AnswersDto? = transformed[UUID.fromString("00000000-0000-0000-0000-000000000000")]
    with(answersDto?.answers!!) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(listOf("FOO", "BAR"))
    }
  }

  @Test
  fun `handles no answer values`() {
    val testAnswers = """
     {
        "answers": {
          "00000000-0000-0000-0000-000000000000": []
        }
    }
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, UpdateAssessmentEpisodeDto::class.java)
    val transformed = deserialized.asAnswersDtos()
    val answersDto: AnswersDto? = transformed[UUID.fromString("00000000-0000-0000-0000-000000000000")]
    with(answersDto?.answers!!) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(emptyList<AnswerDto>())
    }
  }

  @Test
  fun `filters out blank strings from answers`() {
    val testAnswers = """
     {
        "answers": {
          "00000000-0000-0000-0000-000000000000": [
            ""
          ]
        }
    }
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, UpdateAssessmentEpisodeDto::class.java)
    val transformed = deserialized.asAnswersDtos()
    val answersDto: AnswersDto? = transformed[UUID.fromString("00000000-0000-0000-0000-000000000000")]
    with(answersDto?.answers!!) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(emptyList<AnswerDto>())
    }
  }
}
