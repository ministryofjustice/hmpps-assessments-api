package uk.gov.justice.digital.assessments.jpa.entities

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "ASSESSED_EPISODE")
@TypeDefs(
  TypeDef(name = "json", typeClass = JsonStringType::class),
  TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)
class AssessmentEpisodeEntity(

  @Id
  @Column(name = "EPISODE_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val episodeId: Long? = null,

  @Column(name = "EPISODE_UUID")
  val episodeUuid: UUID? = UUID.randomUUID(),

  @ManyToOne
  @JoinColumn(name = "ASSESSMENT_UUID", referencedColumnName = "ASSESSMENT_UUID")
  val assessment: AssessmentEntity? = null,

  @Column(name = "USER_ID")
  val userId: String? = null,

  @Column(name = "CREATED_DATE")
  val createdDate: LocalDateTime? = null,

  @Column(name = "END_DATE")
  val endDate: LocalDateTime? = null,

  @Column(name = "CHANGE_REASON")
  val changeReason: String? = null,

  @Type(type = "json")
  @Column(columnDefinition = "jsonb", name = "ANSWERS")
  var answers: MutableMap<UUID, AnswerEntity>? = mutableMapOf()

) {
  fun isClosed(): Boolean {
    return endDate != null
  }
}
