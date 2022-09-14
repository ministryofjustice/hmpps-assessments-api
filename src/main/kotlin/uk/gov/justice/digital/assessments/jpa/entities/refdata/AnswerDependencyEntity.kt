package uk.gov.justice.digital.assessments.jpa.entities.refdata

class AnswerDependencyEntity(
  val questionCode: String,
  val triggerQuestionCode: String,
  val operator: Operator,
  val triggerAnswerValue: String,
) {
  enum class Operator {
    NOT,
    IS,
    ANY,
  }
}
