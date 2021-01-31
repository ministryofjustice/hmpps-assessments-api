package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "question_dependency")
class QuestionDependencyEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dependency_id")
    val dependencyId: Long,

    @Column(name = "subject_question_uuid")
    val subjectQuestionUuid: UUID,

    @Column(name = "trigger_question_uuid")
    val triggerQuestionUuid: UUID,

    @Column(name = "trigger_answer_value")
    val triggerAnswerValue: String,

    @Column(name = "dependency_start")
    val startDate: LocalDateTime? = null,

    @Column(name = "dependency_end")
    val endDate: LocalDateTime? = null,

    @Column(name = "display_inline")
    val displayInline : Boolean,
)