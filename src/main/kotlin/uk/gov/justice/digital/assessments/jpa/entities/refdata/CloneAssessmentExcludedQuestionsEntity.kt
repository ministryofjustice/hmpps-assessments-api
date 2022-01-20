package uk.gov.justice.digital.assessments.jpa.entities.refdata

import uk.gov.justice.digital.assessments.jpa.entities.AssessmentSchemaCode
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "clone_assessment_excluded_questions", schema = "hmppsassessmentsschemas")
class CloneAssessmentExcludedQuestionsEntity(
  @Id
  @Column(name = "clone_assessment_excluded_questions_id")
  val cloneAssessmentExcludedQuestionsId: Long,

  @Column(name = "assessment_schema_code")
  @Enumerated(EnumType.STRING)
  val assessmentSchemaCode: AssessmentSchemaCode,

  @Column(name = "question_code")
  val questionCode: String,

) : Serializable
