package uk.gov.justice.digital.assessments.jpa.entities.assessments

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
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
import javax.persistence.Transient

@Entity
@Table(name = "assessed_episode", schema = "hmppsassessmentsapi")
@TypeDefs(
  TypeDef(name = "json", typeClass = JsonStringType::class),
  TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
data class AssessmentEpisodeEntity(

  @Id
  @Column(name = "episode_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val episodeId: Long? = null,

  @Column(name = "episode_uuid")
  val episodeUuid: UUID = UUID.randomUUID(),

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "assessment_uuid")
  val assessment: AssessmentEntity,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "oasys_set_pk")
  val oasysSetPk: Long? = null,

  @ManyToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "author_uuid", referencedColumnName = "author_uuid")
  var author: AuthorEntity,

  @Column(name = "created_date")
  val createdDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "end_date")
  var endDate: LocalDateTime? = null,

  @Column(name = "change_reason")
  val changeReason: String? = null,

  @ManyToOne(cascade = [CascadeType.ALL])
  @JoinColumn(name = "offence_uuid", referencedColumnName = "offence_uuid")
  val offence: OffenceEntity? = null,

  @Type(type = "json")
  @Column(columnDefinition = "jsonb", name = "answers")
  var answers: Answers = mutableMapOf(),

//  @Type(type = "json")
//  @Column(columnDefinition = "jsonb", name = "tables")
//  var tables: Tables = mutableMapOf(),

  @Column(name = "last_edited_date")
  var lastEditedDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "closed_date")
  var closedDate: LocalDateTime? = null,

  @Transient
  var prepopulatedFromOASys: Boolean = false
) {
  fun isComplete(): Boolean {
    return endDate != null
  }

  fun complete() {
    endDate = LocalDateTime.now()
  }

  fun isClosed(): Boolean {
    return closedDate != null
  }

  fun close() {
    closedDate = LocalDateTime.now()
  }

  fun addAnswer(questionCode: String, answers: List<String>) {
    this.answers[questionCode] = this.answers[questionCode].orEmpty().plus(answers).toSet().toList()
  }
}
