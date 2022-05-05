package uk.gov.justice.digital.assessments.jpa.entities.refdata

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "question_dependency", schema = "hmppsassessmentsschemas")
class QuestionDependencyEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "dependency_id")
  val dependencyId: Long,

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subject_question_uuid", referencedColumnName = "question_uuid")
  val subjectQuestionSchema: QuestionEntity,

  @Column(name = "trigger_question_uuid")
  val triggerQuestionUuid: UUID,

  @Column(name = "trigger_answer_value")
  val triggerAnswerValue: String,

  @Column(name = "dependency_start")
  val startDate: LocalDateTime? = null,

  @Column(name = "dependency_end")
  val endDate: LocalDateTime? = null,

  @Column(name = "display_inline")
  val displayInline: Boolean,
)
