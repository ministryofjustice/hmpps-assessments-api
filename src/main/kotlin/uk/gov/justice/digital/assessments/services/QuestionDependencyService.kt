package uk.gov.justice.digital.assessments.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.ConditionalsSchemaDto
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionDependencyEntity
import uk.gov.justice.digital.assessments.jpa.repositories.refdata.QuestionDependencyRepository
import java.util.UUID

@Service
class QuestionDependencyService(
  private val questionDependencyRepository: QuestionDependencyRepository,
) {
  companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  fun dependencies(): QuestionDependencies {
    log.debug("Getting all QuestionDependencies")
    val qd = questionDependencyRepository.findAll()
    log.debug("Getting all QuestionDependencies - end")
    return QuestionDependencies(qd as Collection<QuestionDependencyEntity>)
  }
}

typealias AnswerDependencies = (String?) -> Collection<ConditionalsSchemaDto>?

class QuestionDependencies(questionDeps: Collection<QuestionDependencyEntity>) {
  private val subjects = questionDeps.map { it.subjectQuestionSchema.questionUuid }
  private val triggers = makeTriggers(questionDeps)

  private fun makeTriggers(questionDeps: Collection<QuestionDependencyEntity>): Map<Pair<UUID, String>, Set<ConditionalsSchemaDto>> {
    val triggers = mutableMapOf<Pair<UUID, String>, MutableSet<ConditionalsSchemaDto>>()
    for (dep in questionDeps) {
      val trigger = Pair(dep.triggerQuestionUuid, dep.triggerAnswerValue)
      if (triggers.containsKey(trigger)) {
        triggers[trigger]?.add(ConditionalsSchemaDto(dep.subjectQuestionSchema.questionCode, dep.displayInline))
      } else {
        triggers[trigger] = mutableSetOf(ConditionalsSchemaDto(dep.subjectQuestionSchema.questionCode, dep.displayInline))
      }
    }
    return triggers
  }

  fun hasDependency(subjectUuid: UUID): Boolean = subjects.contains(subjectUuid)

  fun triggersDependency(triggerUuid: UUID, answerValue: String?): Set<ConditionalsSchemaDto>? = triggers[Pair(triggerUuid, answerValue)]

  fun answerTriggers(triggerUuid: UUID): AnswerDependencies = { answerValue: String? -> triggersDependency(triggerUuid, answerValue) }
}
