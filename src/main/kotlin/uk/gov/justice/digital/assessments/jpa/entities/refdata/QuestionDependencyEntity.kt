package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

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
