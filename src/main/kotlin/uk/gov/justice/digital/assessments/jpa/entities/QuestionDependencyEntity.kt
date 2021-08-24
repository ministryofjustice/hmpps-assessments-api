package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "question_dependency")
class QuestionDependencyEntity(
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "dependency_id")
  val dependencyId: Long,

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subject_question_uuid", referencedColumnName = "question_schema_uuid")
  val subjectQuestionSchema: QuestionSchemaEntity,

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
