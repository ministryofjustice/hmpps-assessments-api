package uk.gov.justice.digital.assessments.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UpdateAssessmentEpisodeDtoTest {
  @Test
  fun `converts answers to AnswerDto`() {
    val testAnswers = """
     {
        "answers": {
          "question_code": [
            "FOO",
            "BAR"
          ]
        }
    }
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, UpdateAssessmentEpisodeDto::class.java)
    val transformed = deserialized.asAnswersDtos()
    val answersDto: AnswersDto? = transformed["question_code"]
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
          "question_code": []
        }
    }
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, UpdateAssessmentEpisodeDto::class.java)
    val transformed = deserialized.asAnswersDtos()
    val answersDto: AnswersDto? = transformed["question_code"]
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
          "question_code": [
            ""
          ]
        }
    }
    """.trimIndent()

    val deserialized = ObjectMapper().readValue(testAnswers, UpdateAssessmentEpisodeDto::class.java)
    val transformed = deserialized.asAnswersDtos()
    val answersDto: AnswersDto? = transformed["question_code"]
    with(answersDto?.answers!!) {
      assertThat(size).isEqualTo(1)
      assertThat(first().items).isEqualTo(emptyList<AnswerDto>())
    }
  }
}
