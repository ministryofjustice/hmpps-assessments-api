package uk.gov.justice.digital.assessments.jpa.entities.refdata

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
@Table(name = "question", schema = "hmppsassessmentsschemas")
class QuestionEntity(

  @Id
  @Column(name = "question_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val questionId: Long,

  @Column(name = "question_uuid")
  val questionUuid: UUID = UUID.randomUUID(),

  @Column(name = "question_code")
  val questionCode: String,

  @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
  val externalSources: Collection<ExternalSourceQuestionMappingEntity> = emptyList(),

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

  @ManyToOne
  @JoinColumn(name = "answer_group_uuid", referencedColumnName = "answer_group_uuid")
  val answerGroup: AnswerGroupEntity? = null,

) : Serializable {
  val answerEntities: Collection<AnswerEntity>
    get() = answerGroup?.answerEntities ?: emptyList()
}
