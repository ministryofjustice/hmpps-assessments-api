package uk.gov.justice.digital.assessments.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import tools.jackson.databind.ObjectMapper
import uk.gov.justice.digital.assessments.api.assessments.UpdateAssessmentEpisodeDto

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
    val transformed = deserialized.answers
    val answers: List<Any>? = transformed["question_code"]
    assertThat(answers).isEqualTo(listOf("FOO", "BAR"))
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
    val transformed = deserialized.answers
    val answers: List<Any>? = transformed["question_code"]
    assertThat(answers).isEqualTo(emptyList<String>())
  }
}
