package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import uk.gov.justice.digital.assessments.services.dto.PredictorType
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "predictor_field_mapping", schema = "hmppsassessmentsschemas")
class PredictorFieldMappingEntity(
  @Id
  @Column(name = "predictor_mapping_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val predictorMappingId: Long,

  @Column(name = "predictor_mapping_uuid")
  val predictorMappingUuid: UUID,

  @ManyToOne
  @JoinColumn(name = "question_uuid", referencedColumnName = "question_uuid")
  val questionSchema: QuestionEntity,

  @Column(name = "predictor_type")
  @Enumerated(EnumType.STRING)
  val predictorType: PredictorType,

  @Column(name = "predictor_field_name")
  val predictorFieldName: String,
) : Serializable
