package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.jpa.entities.assessments.AssessmentEntity
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
@Table(name = "external_source_question_mapping", schema = "hmppsassessmentsschemas")
class ExternalSourceQuestionMappingEntity(

  @Id
  @Column(name = "external_source_question_mapping_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val externalSourceQuestionMappingId: Long,

  @Column(name = "external_source_question_mapping_uuid")
  val externalSourceQuestionMappingUuid: UUID = UUID.randomUUID(),

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "external_source")
  val externalSource: String,

  @Column(name = "json_path_field")
  val jsonPathField: String,

  @Column(name = "field_type")
  val fieldType: String,

  @ManyToOne
  @JoinColumn(name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
  val questionSchema: QuestionSchemaEntity? = null,
) : Serializable