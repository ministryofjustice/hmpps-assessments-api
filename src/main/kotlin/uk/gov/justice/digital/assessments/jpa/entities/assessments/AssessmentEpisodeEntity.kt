package uk.gov.justice.digital.assessments.jpa.entities.assessments

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import java.time.LocalDateTime
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
@Table(name = "assessed_episode", schema = "hmppsassessmentsapi")
@TypeDefs(
  TypeDef(name = "json", typeClass = JsonStringType::class),
  TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
class AssessmentEpisodeEntity(

  @Id
  @Column(name = "episode_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val episodeId: Long? = null,

  @Column(name = "episode_uuid")
  val episodeUuid: UUID = UUID.randomUUID(),

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "assessment_uuid")
  val assessment: AssessmentEntity? = null,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "oasys_set_pk")
  val oasysSetPk: Long? = null,

  @Column(name = "user_id")
  val userId: String? = null,

  @Column(name = "created_date")
  val createdDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "end_date")
  var endDate: LocalDateTime? = null,

  @Column(name = "change_reason")
  val changeReason: String? = null,

  @Column(name = "offence_code")
  val offenceCode: String? = null,

  @Column(name = "code_description")
  val codeDescription: String? = null,

  @Column(name = "offence_subcode")
  val offenceSubCode: String? = null,

  @Column(name = "subcode_description")
  val subCodeDescription: String? = null,

  @Type(type = "json")
  @Column(columnDefinition = "jsonb", name = "answers")
  var answers: MutableMap<String, AnswerEntity>? = mutableMapOf(),

  @Type(type = "json")
  @Column(columnDefinition = "jsonb", name = "tables")
  var tables: MutableMap<String, MutableList<Map<String, List<String>>>>? = mutableMapOf(),
) {
  fun isClosed(): Boolean {
    return endDate != null
  }

  fun close() {
    endDate = LocalDateTime.now()
  }
}
