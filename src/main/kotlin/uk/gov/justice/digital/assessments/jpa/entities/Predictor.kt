package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "predictor_assessments")
class Predictor(
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "predictor_type")
  @Enumerated(EnumType.STRING)
  val predictorType: PredictorType,

  @OneToMany
  @JoinColumn(name = "predictor_type", referencedColumnName = "predictor_type")
  val predictorFields: Collection<PredictorFieldMapping> = emptyList(),
) : Serializable
