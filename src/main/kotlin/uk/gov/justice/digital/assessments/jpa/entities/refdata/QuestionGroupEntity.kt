package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.refdata.GroupEntity
import uk.gov.justice.digital.assessments.jpa.entities.refdata.QuestionSchemaEntity
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

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

  @Column(name = "mandatory")
  val mandatory: Boolean = false,

  @Column(name = "validation")
  val validation: String? = null,

  @Column(name = "read_only")
  val readOnly: Boolean = false,

  @Transient
  var question: QuestionSchemaEntity?,

  @Transient
  var nestedGroup: GroupEntity?
) : Serializable
