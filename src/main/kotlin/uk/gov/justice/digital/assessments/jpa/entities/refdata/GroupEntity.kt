package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "grouping", schema = "hmppsassessmentsschemas")
class GroupEntity(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "group_id")
  val groupId: Long,

  @Column(name = "group_uuid")
  val groupUuid: UUID = UUID.randomUUID(),

  @Column(name = "group_code")
  val groupCode: String,

  @Column(name = "heading")
  val heading: String? = null,

  @Column(name = "subheading")
  val subheading: String? = null,

  @Column(name = "help_text")
  val helpText: String? = null,

  @Column(name = "group_start")
  val groupStart: LocalDateTime? = null,

  @Column(name = "group_end")
  val groupEnd: LocalDateTime? = null,

  @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
  val contents: Collection<QuestionGroupEntity> = emptyList(),
) : Serializable
