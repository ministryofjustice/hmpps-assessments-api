package uk.gov.justice.digital.assessments.testutils

import org.assertj.core.api.Assertions
import uk.gov.justice.digital.assessments.api.AnswerDto
import uk.gov.justice.digital.assessments.api.AnswersDto

class Verify {
  companion object {
    fun emptyAnswer(questionAnswer: AnswersDto) {
      val answers = questionAnswer.answers
      Assertions.assertThat(answers.size).isEqualTo(0)
    }
    fun singleAnswer(questionAnswer: AnswersDto, vararg expected: String) {
      val answers = questionAnswer.answers
      Assertions.assertThat(answers.size).isEqualTo(1)
      val expectedAnswer = AnswerDto(expected.toList())
      Assertions.assertThat(answers.first()).isEqualTo(expectedAnswer)
    }
    fun multiAnswers(questionAnswer: AnswersDto, vararg expected: String) {
      val answers = questionAnswer.answers
      Assertions.assertThat(answers.size).isEqualTo(expected.size)
      val expectedAnswers = expected.map { AnswerDto(listOf(it)) }
      Assertions.assertThat(answers).isEqualTo(expectedAnswers)
    }
  }
}