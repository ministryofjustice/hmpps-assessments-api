package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "answer_group", schema = "hmppsassessmentsschemas")
class AnswerGroupEntity(
  @Id
  @Column(name = "answer_group_id")
  val answerGroupId: Long,

  @Column(name = "answer_group_uuid")
  val answerGroupUuid: UUID = UUID.randomUUID(),

  @Column(name = "answer_group_code")
  val answerGroupCode: String? = null,

  @Column(name = "group_start")
  val groupStart: LocalDateTime? = null,

  @Column(name = "group_end")
  val groupEnd: LocalDateTime? = null,

  @OneToMany(mappedBy = "answerGroup", fetch = FetchType.EAGER)
  val answerEntities: Collection<AnswerEntity> = emptyList(),
) : Serializable
