package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "ASSESSMENT")
class AssessmentEntity(

        @Id
        @Column(name = "ASSESSMENT_ID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val assessmentId: Long? = null,

        @Column(name = "SUPERVISION_ID")
        val supervisionId: String? = null,

        @Column(name = "CREATED_DATE")
        val createdDate: LocalDateTime? = null,

        @Column(name = "COMPLETED_DATE")
        val completedDate: LocalDateTime? = null,

        @OneToMany(mappedBy = "assessment", cascade = [CascadeType.ALL])
        val episodes: MutableCollection<AssessmentEpisodeEntity> = mutableListOf()

) {
    fun getCurrentEpisode(): AssessmentEpisodeEntity? {
        return episodes.firstOrNull { it.endDate == null }
    }

    fun newEpisode(changeReason: String, user: String? = "anonymous"): AssessmentEpisodeEntity {
        val currentEpisode = getCurrentEpisode()
        if (currentEpisode != null) {
            return currentEpisode
        }
        val newEpisode = AssessmentEpisodeEntity(assessment = this, createdDate = LocalDateTime.now(), changeReason = changeReason, userId = user)
        episodes.add(newEpisode)
        return newEpisode;
    }
}
