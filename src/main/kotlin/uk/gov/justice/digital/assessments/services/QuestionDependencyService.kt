package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.QuestionDependencyEntity
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionDependencyRepository
import java.util.UUID

@Service
class QuestionDependencyService(
  private val questionDependencyRepository: QuestionDependencyRepository
) {
  fun dependencies(): QuestionDependencies {
    val qd = questionDependencyRepository.findAll()

    return QuestionDependencies(qd)
  }
}

typealias AnswerDependencies = (String?) -> UUID?

class QuestionDependencies(questionDeps: Collection<QuestionDependencyEntity>) {
  private val subjects = questionDeps.map { it.subjectQuestionUuid }
  private val triggers = questionDeps.associateBy(
    { Pair(it.triggerQuestionUuid, it.triggerAnswerValue) },
    { it.subjectQuestionUuid }
  )
  private val displayTypes = questionDeps.associateBy(
    { Pair(it.triggerQuestionUuid, it.triggerAnswerValue) },
    { it.displayInline }
  )

  fun hasDependency(subjectUuid: UUID): Boolean = subjects.contains(subjectUuid)
  fun triggersDependency(triggerUuid: UUID, answerValue: String?): UUID? =
    triggers[Pair(triggerUuid, answerValue)]

  private fun getDisplayType(triggerUuid: UUID, answerValue: String?): Boolean? =
    displayTypes[Pair(triggerUuid, answerValue)]

  fun answerTriggers(triggerUuid: UUID): AnswerDependencies {
    return { answerValue: String? -> triggersDependency(triggerUuid, answerValue) }

  }

  fun getDisplayType(triggerUuid: UUID): (String?) -> Boolean? {
    return { answerValue: String? -> getDisplayType(triggerUuid, answerValue) }
  }
}