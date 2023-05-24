package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

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
  val text: String? = null,
)
