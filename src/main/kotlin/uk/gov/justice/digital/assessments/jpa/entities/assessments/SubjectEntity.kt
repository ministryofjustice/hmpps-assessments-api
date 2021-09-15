package uk.gov.justice.digital.assessments.jpa.entities.assessments

import java.io.Serializable
import java.time.LocalDate
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
@Table(name = "subject", schema = "hmppsassessmentsapi")
class SubjectEntity(
  @Id
  @Column(name = "subject_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val subjectId: Long? = null,

  @Column(name = "subject_uuid")
  val subjectUuid: UUID = UUID.randomUUID(),

  @Column(name = "name")
  val name: String? = null,

  @Column(name = "oasys_offender_pk")
  val oasysOffenderPk: Long? = null,

  @Column(name = "pnc")
  val pnc: String? = null,

  @Column(name = "crn")
  val crn: String,

  @Column(name = "date_of_birth")
  val dateOfBirth: LocalDate,

  @Column(name = "gender")
  val gender: String? = null,

  @Column(name = "created_date")
  val createdDate: LocalDateTime? = null,

  @ManyToOne
  @JoinColumn(name = "assessment_uuid", referencedColumnName = "assessment_uuid")
  val assessment: AssessmentEntity? = null,
) : Serializable
