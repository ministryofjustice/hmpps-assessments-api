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
@Table(name = "assessment_schema_groups", schema = "hmppsassessmentsschemas")
class AssessmentSchemaGroupsEntity(
  @Id
  @Column(name = "assessment_schema_group_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentSchemaGroupId: Long,

  @Column(name = "assessment_schema_uuid")
  val assessmentSchemaUuid: UUID,

  @ManyToOne
  @JoinColumn(name = "group_uuid", referencedColumnName = "group_uuid")
  val group: GroupEntity,

  ) : Serializable
