package uk.gov.justice.digital.assessments.jpa.entities.refdata

import java.io.Serializable
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "oasys_question_mapping", schema = "hmppsassessmentsschemas")
class OASysMappingEntity(
  @Id
  @Column(name = "mapping_id")
  val mappingId: Long,

  @Column(name = "mapping_uuid")
  val mappingUuid: UUID = UUID.randomUUID(),

  @Column(name = "ref_section_code")
  val sectionCode: String,

  @Column(name = "logical_page")
  val logicalPage: Long? = null,

  @Column(name = "ref_question_code")
  val questionCode: String,

  @Column(name = "fixed_field")
  private val fixed_field: Boolean? = false,

  @ManyToOne
  @JoinColumn(name = "question_schema_uuid", referencedColumnName = "question_schema_uuid")
  val questionSchema: QuestionSchemaEntity
) : Serializable {
  val isFixed: Boolean get() = (fixed_field == true)
}
