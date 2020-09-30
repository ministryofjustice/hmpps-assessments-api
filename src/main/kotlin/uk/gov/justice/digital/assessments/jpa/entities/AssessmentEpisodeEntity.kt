package uk.gov.justice.digital.assessments.jpa.entities

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "ASSESSED_EPISODE")
class AssessmentEpisodeEntity (

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
        val changeReason: String? = null

)
