package uk.gov.justice.digital.assessments.jpa.entities.refdata

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity(name = "reference_data_target")
@Table(name = "oasys_reference_data_target_mapping", schema = "hmppsassessmentsschemas")
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
