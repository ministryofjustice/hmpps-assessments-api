package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.services.dto.PredictorType
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
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "assessment_predictors", schema = "hmppsassessmentsschemas")
class PredictorEntity(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "predictor_type")
  @Enumerated(EnumType.STRING)
  val type: PredictorType,

  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "predictor_type", referencedColumnName = "predictor_type")
  val fieldEntities: Collection<PredictorFieldMappingEntity> = emptyList(),
) : Serializable
