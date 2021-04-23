package uk.gov.justice.digital.assessments.jpa.entities

import javax.persistence.*

@Entity(name = "reference_data_target")
@Table(name = "REFERENCE_DATA_TARGET_MAPPING")
class ReferenceDataTargetMappingEntity(

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val id: Long,

  @ManyToOne
  @JoinColumn(name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
  val questionSchema: QuestionSchemaEntity,

  @ManyToOne
  @JoinColumn(name = "parent_question_schema_uuid", referencedColumnName = "question_schema_uuid")
  val parentQuestionSchema: QuestionSchemaEntity,

  @Column(name = "is_required")
  val isRequired: Boolean = false,

)