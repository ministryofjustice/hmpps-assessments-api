package uk.gov.justice.digital.assessments.jpa.entities.assessments

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

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

  fun getCurrentEpisode(): AssessmentEpisodeEntity? = episodes.firstOrNull { !it.isComplete() && !it.isClosed() }

  fun hasCurrentEpisode(): Boolean = episodes.indexOfFirst { !it.isComplete() && !it.isClosed() } >= 0

  fun getLatestInProgressOrCompleteEpisodeOfType(assessmentType: AssessmentType): AssessmentEpisodeEntity? = episodes.filter { it.assessmentType == assessmentType && !it.isClosed() }
    .maxByOrNull { it.createdDate }

  fun newEpisode(
    changeReason: String,
    assessmentType: AssessmentType,
    offence: OffenceEntity?,
    author: AuthorEntity,
  ): AssessmentEpisodeEntity {
    val currentEpisode = getCurrentEpisode()
    if (currentEpisode != null) {
      return currentEpisode
    }
    val newEpisode = AssessmentEpisodeEntity(
      assessment = this,
      createdDate = LocalDateTime.now(),
      changeReason = changeReason,
      author = author,
      assessmentType = assessmentType,
      offence = offence,
    )
    episodes.add(newEpisode)
    return newEpisode
  }
}
