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
@Table(name = "answer_schema", schema = "hmppsassessmentsschemas")
class AnswerSchemaEntity(
  @Id
  @Column(name = "answer_schema_id")
  val answerSchemaId: Long,

  @Column(name = "answer_schema_uuid")
  val answerSchemaUuid: UUID = UUID.randomUUID(),

  @Column(name = "answer_schema_code")
  val answerSchemaCode: String? = null,

  @ManyToOne
  @JoinColumn(name = "answer_schema_group_uuid", referencedColumnName = "answer_schema_group_uuid")
  val answerSchemaGroup: AnswerSchemaGroupEntity,

  @Column(name = "answer_start")
  val answerStart: LocalDateTime? = null,

  @Column(name = "answer_end")
  val answerEnd: LocalDateTime? = null,

  @Column(name = "value")
  val value: String? = null,

  @Column(name = "text")
  val text: String? = null
)
