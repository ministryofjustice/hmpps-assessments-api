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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import java.io.Serializable

@Entity
@Table(name = "assessment_predictors", schema = "hmppsassessmentsschemas")
class PredictorEntity(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long,

  @Column(name = "assessment_type")
  @Enumerated(EnumType.STRING)
  val assessmentType: AssessmentType,

  @Column(name = "predictor_type")
  @Enumerated(EnumType.STRING)
  val type: PredictorType,

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "predictor_type", referencedColumnName = "predictor_type")
  val fieldEntities: Collection<PredictorFieldMappingEntity> = emptyList(),
) : Serializable
