package uk.gov.justice.digital.assessments.services

import org.springframework.stereotype.Service
import uk.gov.justice.digital.assessments.jpa.entities.QuestionDependencyEntity
import uk.gov.justice.digital.assessments.jpa.repositories.QuestionDependencyRepository
import java.util.*

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

    fun hasDependency(subjectUuid: UUID): Boolean = subjects.contains(subjectUuid)
    fun triggersDependency(triggerUuid: UUID, answerValue: String?): UUID? =
            triggers.get(Pair(triggerUuid, answerValue))
    fun answerTriggers(triggerUuid: UUID): AnswerDependencies {
        return {  answerValue: String? -> triggersDependency(triggerUuid, answerValue) }
    }
}