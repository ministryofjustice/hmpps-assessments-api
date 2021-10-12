package uk.gov.justice.digital.assessments.jpa.entities.assessments

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import uk.gov.justice.digital.assessments.utils.RequestData
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.CascadeType
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

@Entity
@Table(name = "assessment", schema = "hmppsassessmentsapi")
class AssessmentEntity(

  @Id
  @Column(name = "assessment_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentId: Long? = null,

  @Column(name = "assessment_uuid")
  val assessmentUuid: UUID = UUID.randomUUID(),

  @Column(name = "created_date")
  val createdDate: LocalDateTime = LocalDateTime.now(),

  @Column(name = "completed_date")
  var completedDate: LocalDateTime? = null,

  @OneToMany(mappedBy = "assessment", cascade = [CascadeType.ALL])
  val episodes: MutableList<AssessmentEpisodeEntity> = mutableListOf(),

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "subject_uuid", referencedColumnName = "subject_uuid")
  val subject: SubjectEntity? = null,
) : Serializable {

  fun getCurrentEpisode(): AssessmentEpisodeEntity? {
    return episodes.firstOrNull { !it.isClosed() }
  }

  fun getLatestClosedEpisodeOfType(assessmentSchemaCode: AssessmentSchemaCode): AssessmentEpisodeEntity? {
    return episodes.filter { it.assessmentSchemaCode == assessmentSchemaCode && it.isClosed() }
      .maxByOrNull { it.endDate!! }
  }

  fun newEpisode(
    changeReason: String,
    oasysSetPk: Long? = null,
    assessmentSchemaCode: AssessmentSchemaCode,
    offence: OffenceEntity?
  ): AssessmentEpisodeEntity {
    val currentEpisode = getCurrentEpisode()
    if (currentEpisode != null) {
      return currentEpisode
    }
    val newEpisode = AssessmentEpisodeEntity(
      assessment = this,
      createdDate = LocalDateTime.now(),
      changeReason = changeReason,
      userName = RequestData.getUserName(),
      oasysSetPk = oasysSetPk,
      assessmentSchemaCode = assessmentSchemaCode,
      offence = offence
    )
    episodes.add(newEpisode)
    return newEpisode
  }
}
