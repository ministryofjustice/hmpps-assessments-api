package uk.gov.justice.digital.assessments.jpa.entities

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
@Table(name = "SUBJECT")
class SubjectEntity(
  @Id
  @Column(name = "SUBJECT_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val subjectId: Long? = null,

  @Column(name = "SUBJECT_UUID")
  val subjectUuid: UUID = UUID.randomUUID(),

  @Column(name = "SOURCE")
  val source: String? = null,

  @Column(name = "SOURCE_ID")
  val sourceId: String? = null,

  @Column(name = "NAME")
  val name: String? = null,

  @Column(name = "OASYS_OFFENDER_PK")
  val oasysOffenderPk: Long? = null,

  @Column(name = "PNC")
  val pnc: String? = null,

  @Column(name = "CRN")
  val crn: String? = null,

  @Column(name = "DATE_OF_BIRTH")
  val dateOfBirth: LocalDate? = null,

  @Column(name = "CREATED_DATE")
  val createdDate: LocalDateTime? = null,

  @ManyToOne
  @JoinColumn(name = "ASSESSMENT_UUID", referencedColumnName = "ASSESSMENT_UUID")
  val assessment: AssessmentEntity? = null,
) : Serializable
