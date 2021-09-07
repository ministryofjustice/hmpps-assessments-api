package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "assessment_schema", schema = "hmppsassessmentsschemas")
class AssessmentSchemaEntity(
  @Id
  @Column(name = "assessment_schema_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentSchemaId: Long,

  @ManyToOne
  @JoinColumn(name = "assessment_schema_uuid", referencedColumnName = "assessment_schema_uuid")
  val assessmentSchemaGroup: AssessmentSchemaGroupsEntity,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "oasys_assessment_type")
  @Enumerated(EnumType.STRING)
  val oasysAssessmentType: OasysAssessmentType? = null,

  @Column(name = "oasys_create_assessment_at")
  @Enumerated(EnumType.STRING)
  val oasysCreateAssessmentAt: OasysCreateAssessmentAt? = null,

  @Column(name = "assessment_name")
  val assessmentName: String? = null,

  @OneToMany
  @JoinColumn(name = "assessment_schema_code", referencedColumnName = "assessment_schema_code")
  val predictorEntities: Collection<PredictorEntity> = emptyList(),
) : Serializable
