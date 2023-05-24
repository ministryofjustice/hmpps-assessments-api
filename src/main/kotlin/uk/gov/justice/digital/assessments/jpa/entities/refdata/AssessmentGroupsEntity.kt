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
