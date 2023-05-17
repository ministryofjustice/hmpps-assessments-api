package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import java.io.Serializable

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

  @Column(name = "assessment_name")
  val assessmentName: String? = null,

  @OneToMany(fetch = FetchType.LAZY)
  @JoinColumn(name = "assessment_type", referencedColumnName = "assessment_type")
  val cloneAssessmentExcludedQuestionsEntities: Collection<CloneAssessmentExcludedQuestionsEntity> = emptyList(),
) : Serializable
