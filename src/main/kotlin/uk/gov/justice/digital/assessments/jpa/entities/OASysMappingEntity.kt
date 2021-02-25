package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "oasys_question_mapping")
class OASysMappingEntity(
  @Id
  @Column(name = "mapping_id")
  val mappingId: Long,

  @Column(name = "mapping_uuid")
  val mappingUuid: UUID = UUID.randomUUID(),

  @Column(name = "ref_section_code")
  val sectionCode: String? = null,

  @Column(name = "logical_page")
  val logicalPage: String? = null,

  @Column(name = "ref_question_code")
  val questionCode: String? = null,

  @Column(name = "fixed_field")
  private val fixed_field: Boolean? = false,

  @ManyToOne
  @JoinColumn(name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
  val questionSchema: QuestionSchemaEntity
) : Serializable {
  val isFixed: Boolean get()= (fixed_field == true)
}