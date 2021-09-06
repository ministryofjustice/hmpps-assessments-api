package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.refdata.AnswerSchemaEntity
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "ANSWER_SCHEMA_GROUP")
class AnswerSchemaGroupEntity(
  @Id
  @Column(name = "answer_schema_group_id")
  val answerSchemaId: Long,

  @Column(name = "answer_schema_group_uuid")
  val answerSchemaGroupUuid: UUID = UUID.randomUUID(),

  @Column(name = "answer_schema_group_code")
  val answerSchemaGroupCode: String? = null,

  @Column(name = "group_start")
  val groupStart: LocalDateTime? = null,

  @Column(name = "group_end")
  val groupEnd: LocalDateTime? = null,

  @OneToMany(mappedBy = "answerSchemaGroup", fetch = FetchType.EAGER)
  val answerSchemaEntities: Collection<AnswerSchemaEntity> = emptyList()
) : Serializable
