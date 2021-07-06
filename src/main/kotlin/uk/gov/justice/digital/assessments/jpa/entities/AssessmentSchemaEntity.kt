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
class AssessmentSchemaEntity {
  @Id
  @Column(name = "ASSESSMENT_SCHEMA_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  val assessmentSchemaId: Long? = null,

  @Column(name = "ASSESSMENT_SCHEMA_UUID")
  val assessmentSchemaUuid: UUID = UUID.randomUUID(),

  @Column(name = "ASSESSMENT_SCHEMA_CODE")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode? = null,

  @Column(name = "OASYS_CREATE_ASSESSMENT_AT")
  @Enumerated(EnumType.STRING)
  val oasysCreateAssessmentAt: AssessmentSchemaCode? = null,
}

CREATE TABLE IF NOT EXISTS assessment_schema
(
assessment_schema_id       SERIAL PRIMARY KEY,
assessment_schema_uuid     UUID        NOT NULL unique,
assessment_schema_code     TEXT        NOT NULL,
oasys_assessment_type      VARCHAR(50) NULL,
oasys_create_assessment_at TEXT,
assessment_name            TEXT
);
