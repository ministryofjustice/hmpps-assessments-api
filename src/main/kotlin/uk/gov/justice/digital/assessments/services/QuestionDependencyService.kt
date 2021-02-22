package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.api.ConditionalsSchemaDto
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
  private val triggers = makeTriggers(questionDeps)

  // change this to return a set of conditionalSchemaDTOs instead of a set of pairs
  // then should be able to call that from answerschemadto.kt
  private fun makeTriggers(questionDeps: Collection<QuestionDependencyEntity>): Map<Pair<UUID, String>, Set<ConditionalsSchemaDto>>
  {
    val triggers = mutableMapOf<Pair<UUID,String>, MutableSet<ConditionalsSchemaDto>>()
    for (dep in questionDeps)
    {
      val trigger = Pair(dep.triggerQuestionUuid, dep.triggerAnswerValue)
      if (triggers.containsKey(trigger))
        triggers[trigger]?.add(ConditionalsSchemaDto(dep.subjectQuestionUuid, dep.displayInline))
//        triggers[trigger]?.add(Pair(dep.subjectQuestionUuid, dep.displayInline))
      else
        triggers[trigger] = mutableSetOf(ConditionalsSchemaDto(dep.subjectQuestionUuid, dep.displayInline))
    }
    return triggers
  }

//  private val triggers = questionDeps.associateBy(
//    { Pair(it.triggerQuestionUuid, it.triggerAnswerValue) },
//    { it.subjectQuestionUuid }
//  )
//  private val displayTypes = questionDeps.associateBy(
//    { Pair(it.triggerQuestionUuid, it.triggerAnswerValue) },
//    { it.displayInline }
//  )

//  private val displayTypes = makeDisplayTypes(questionDeps)
//  private fun makeDisplayTypes(questionDeps: Collection<QuestionDependencyEntity>): Map<Pair<UUID, String>, Set<Boolean>>
//  {
//    val displayTypes = mutableMapOf<Pair<UUID,String>, MutableSet<Boolean>>()
//    for (dep in questionDeps)
//    {
//      val displayType = Pair(dep.triggerQuestionUuid, dep.triggerAnswerValue)
//      if (displayTypes.containsKey(displayType))
//        displayTypes[displayType]?.add(dep.displayInline)
//      else
//        displayTypes[displayType] = mutableSetOf(dep.displayInline)
//    }
//    return displayTypes
//  }

  fun hasDependency(subjectUuid: UUID): Boolean = subjects.contains(subjectUuid)

//  fun triggersDependency(triggerUuid: UUID, answerValue: String?): Set<Pair<UUID, Boolean>>? =
//    triggers[Pair(triggerUuid, answerValue)]


  fun triggersDependency(triggerUuid: UUID, answerValue: String?): Set<ConditionalsSchemaDto>? =
    triggers[Pair(triggerUuid, answerValue)]

  fun answerTriggers(triggerUuid: UUID): (String?) -> Set<ConditionalsSchemaDto>? {
    return { answerValue: String? -> triggersDependency(triggerUuid, answerValue) }
  }
}
