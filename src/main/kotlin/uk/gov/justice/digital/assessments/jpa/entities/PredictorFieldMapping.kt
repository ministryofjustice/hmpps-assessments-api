package uk.gov.justice.digital.assessments.jpa.entities

import uk.gov.justice.digital.assessments.services.dto.PredictorType
import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "predictor_field_mapping")
class PredictorFieldMapping(
  @Id
  @Column(name = "predictor_mapping_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val predictorMappingId: Long,

  @Column(name = "predictor_mapping_uuid")
  val predictorMappingUuid: UUID,

  @ManyToOne
  @JoinColumn(name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
  val questionSchema: QuestionSchemaEntity,

  @Column(name = "predictor_type")
  @Enumerated(EnumType.STRING)
  val predictorType: PredictorType,

  @Column(name = "predictor_field_name")
  val predictorFieldName: String,
) : Serializable
