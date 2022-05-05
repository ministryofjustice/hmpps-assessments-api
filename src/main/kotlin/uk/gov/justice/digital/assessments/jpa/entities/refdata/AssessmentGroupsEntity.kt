package uk.gov.justice.digital.assessments.jpa.entities.refdata

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
@Table(name = "assessment_groups", schema = "hmppsassessmentsschemas")
class AssessmentGroupsEntity(
  @Id
  @Column(name = "assessment_group_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentGroupId: Long,

  @Column(name = "assessment_uuid")
  val assessmentUuid: UUID,

  @ManyToOne
  @JoinColumn(name = "group_uuid", referencedColumnName = "group_uuid")
  val group: GroupEntity,

) : Serializable
