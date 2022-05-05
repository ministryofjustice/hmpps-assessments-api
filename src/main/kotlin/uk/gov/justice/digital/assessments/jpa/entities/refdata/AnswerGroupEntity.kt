package uk.gov.justice.digital.assessments.jpa.entities.refdata

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
@Table(name = "answer_group", schema = "hmppsassessmentsschemas")
class AnswerGroupEntity(
  @Id
  @Column(name = "answer_group_id")
  val answerGroupId: Long,

  @Column(name = "answer_group_uuid")
  val answerGroupUuid: UUID = UUID.randomUUID(),

  @Column(name = "answer_group_code")
  val answerSchemaGroupCode: String? = null,

  @Column(name = "group_start")
  val groupStart: LocalDateTime? = null,

  @Column(name = "group_end")
  val groupEnd: LocalDateTime? = null,

  @OneToMany(mappedBy = "answerGroup", fetch = FetchType.EAGER)
  val answerEntities: Collection<AnswerEntity> = emptyList()
) : Serializable
