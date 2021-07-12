package uk.gov.justice.digital.assessments.jpa.entities

import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity(name = "question")
@Table(name = "QUESTION_SCHEMA")
class QuestionSchemaEntity(

  @Id
  @Column(name = "question_schema_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val questionSchemaId: Long,

  @Column(name = "question_schema_uuid")
  val questionSchemaUuid: UUID = UUID.randomUUID(),

  @Column(name = "question_code")
  val questionCode: String? = null,

  @Column(name = "external_source")
  val externalSource: String? = null,

  @Column(name = "question_start")
  val questionStartDate: LocalDateTime? = null,

  @Column(name = "question_end")
  val questionEndDate: LocalDateTime? = null,

  @Column(name = "answer_type")
  val answerType: String? = null,

  @Column(name = "question_text")
  val questionText: String? = null,

  @Column(name = "question_help_text")
  val questionHelpText: String? = null,

  @Column(name = "reference_data_category")
  val referenceDataCategory: String? = null,

  @OneToMany(mappedBy = "questionSchema", fetch = FetchType.LAZY)
  val referenceDataTargets: Collection<ReferenceDataTargetMappingEntity> = emptyList(),

  @ManyToOne
  @JoinColumn(name = "answer_schema_group_uuid", referencedColumnName = "answer_schema_group_uuid")
  val answerSchemaGroup: AnswerSchemaGroupEntity? = null,

  @OneToMany(mappedBy = "questionSchema", fetch = FetchType.LAZY)
  val oasysMappings: Collection<OASysMappingEntity> = emptyList()
) : Serializable {
  val answerSchemaEntities: Collection<AnswerSchemaEntity>
    get() = answerSchemaGroup?.answerSchemaEntities ?: emptyList()
}
