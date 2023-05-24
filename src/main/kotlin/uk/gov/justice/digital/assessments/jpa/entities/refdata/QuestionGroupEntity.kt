package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "question_group", schema = "hmppsassessmentsschemas")
class QuestionGroupEntity(

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_group_id")
  val questionGroupId: Long,

  @Column(name = "question_group_uuid")
  val uuid: UUID = UUID.randomUUID(),

  @ManyToOne
  @JoinColumn(name = "group_uuid", referencedColumnName = "group_uuid")
  val group: GroupEntity,

  @Column(name = "content_uuid")
  val contentUuid: UUID,

  @Column(name = "content_type")
  val contentType: String,

  @Column(name = "display_order")
  val displayOrder: Int = 0,

  @Column(name = "read_only")
  val readOnly: Boolean = false,

  @Transient
  var question: QuestionEntity?,

  @Transient
  var nestedGroup: GroupEntity?,
) : Serializable
