package uk.gov.justice.digital.assessments.testutils

import org.assertj.core.api.Assertions

class Verify {
  companion object {
    fun emptyAnswer(answers: List<String>) {
      Assertions.assertThat(answers.size).isEqualTo(0)
    }
    fun singleAnswer(answers: List<String>, vararg expected: String) {
      Assertions.assertThat(answers.size).isEqualTo(1)
      val expectedAnswer = listOf(expected.first())
      Assertions.assertThat(answers).isEqualTo(expectedAnswer)
    }
    fun multiAnswers(answers: List<String>, vararg expected: String) {
      Assertions.assertThat(answers.size).isEqualTo(expected.size)
      Assertions.assertThat(answers).isEqualTo(expected.toList())
    }
  }
}
