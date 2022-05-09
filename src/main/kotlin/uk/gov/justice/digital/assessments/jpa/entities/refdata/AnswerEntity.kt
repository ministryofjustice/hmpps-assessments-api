package uk.gov.justice.digital.assessments.jpa.entities.refdata

import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "answer", schema = "hmppsassessmentsschemas")
class AnswerEntity(
  @Id
  @Column(name = "answer_id")
  val answerId: Long,

  @Column(name = "answer_uuid")
  val answerUuid: UUID = UUID.randomUUID(),

  @Column(name = "answer_code")
  val answerCode: String? = null,

  @ManyToOne
  @JoinColumn(name = "answer_group_uuid", referencedColumnName = "answer_group_uuid")
  val answerGroup: AnswerGroupEntity,

  @Column(name = "answer_start")
  val answerStart: LocalDateTime? = null,

  @Column(name = "answer_end")
  val answerEnd: LocalDateTime? = null,

  @Column(name = "value")
  val value: String? = null,

  @Column(name = "text")
  val text: String? = null
)
