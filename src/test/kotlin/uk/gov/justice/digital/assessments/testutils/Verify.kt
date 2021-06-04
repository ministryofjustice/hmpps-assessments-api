package uk.gov.justice.digital.assessments.testutils

import org.assertj.core.api.Assertions
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto

class Verify {
  companion object {
    fun answers(questionAnswer: AnswersDto, vararg expected: String) {
      val answers = questionAnswer.answers.toList()
      Assertions.assertThat(answers.size).isEqualTo(expected.size)
      expected.forEachIndexed { index, s ->
        val expectedAnswer = AnswerDto(listOf(s))
        Assertions.assertThat(answers[index]).isEqualTo(expectedAnswer)
      }
    }

  }
}