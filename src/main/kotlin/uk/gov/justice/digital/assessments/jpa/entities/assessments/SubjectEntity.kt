package uk.gov.justice.digital.assessments.jpa.entities.assessments

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "subject", schema = "hmppsassessmentsapi")
data class SubjectEntity(
  @Id
  @Column(name = "subject_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val subjectId: Long? = null,

  @Column(name = "subject_uuid")
  val subjectUuid: UUID = UUID.randomUUID(),

  @Column(name = "name")
  val name: String? = null,

  @Column(name = "pnc")
  val pnc: String? = null,

  @Column(name = "crn")
  val crn: String,

  @Column(name = "date_of_birth")
  val dateOfBirth: LocalDate,

  @Column(name = "gender")
  val gender: String? = null,

  @Column(name = "created_date")
  val createdDate: LocalDateTime? = LocalDateTime.now(),

  @OneToMany(mappedBy = "subject", fetch = FetchType.LAZY)
  val assessments: Collection<AssessmentEntity>? = emptyList(),
) : Serializable {

  fun getCurrentAssessment(): AssessmentEntity? = assessments?.maxByOrNull { it.createdDate }
}
