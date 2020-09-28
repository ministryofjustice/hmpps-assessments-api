package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "ASSESSED_EPISODE")
class AssessmentEpisodeEntity (

        @Id
        @Column(name = "EPISODE_ID")
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val episodeId: Long? = null,

        @ManyToOne
        @JoinColumn(name = "ASSESSMENT_ID", referencedColumnName = "ASSESSMENT_ID")
        val assessment: AssessmentEntity? = null,

        @Column(name = "USER_ID")
        val userId: String? = null,

        @Column(name = "CREATED_DATE")
        val createdDate: LocalDateTime? = null,

        @Column(name = "END_DATE")
        val endDate: LocalDateTime? = null,

        @Column(name = "CHANGE_REASON")
        val changeReason: String? = null

)
