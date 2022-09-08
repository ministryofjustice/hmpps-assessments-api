package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "assessment", schema = "hmppsassessmentsschemas")
class AssessmentEntity(
  @Id
  @Column(name = "assessment_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentId: Long,

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "assessment_uuid")
  val assessmentGroup: AssessmentGroupsEntity,

  @Column(name = "assessment_type")
  @Enumerated(EnumType.STRING)
  val assessmentType: AssessmentType,

  // todo: remove this unused field from the db with flyway migration
  @Column(name = "oasys_assessment_type")
  val oasysAssessmentType: String? = null,

  @Column(name = "oasys_create_assessment_at")
  @Enumerated(EnumType.STRING)
  val oasysCreateAssessmentAt: OasysCreateAssessmentAt? = null,

  @Column(name = "assessment_name")
  val assessmentName: String? = null,

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_type", referencedColumnName = "assessment_type")
  val cloneAssessmentExcludedQuestionsEntities: Collection<CloneAssessmentExcludedQuestionsEntity> = emptyList(),
) : Serializable
