package uk.gov.justice.digital.assessments.jpa.entities

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "ASSESSMENT_SCHEMA")
class AssessmentSchemaEntity(
  @Id
  @Column(name = "ASSESSMENT_SCHEMA_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentSchemaId: Long,

  @Column(name = "ASSESSMENT_SCHEMA_UUID")
  val assessmentSchemaUuid: UUID = UUID.randomUUID(),

  @Column(name = "ASSESSMENT_SCHEMA_CODE")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "OASYS_ASSESSMENT_TYPE")
  @Enumerated(EnumType.STRING)
  val oasysAssessmentType: OasysAssessmentType? = null,

  @Column(name = "OASYS_CREATE_ASSESSMENT_AT")
  @Enumerated(EnumType.STRING)
  val oasysCreateAssessmentAt: OasysCreateAssessmentAt? = null,

  @Column(name = "ASSESSMENT_NAME")
  val assessmentName: String? = null,
)
