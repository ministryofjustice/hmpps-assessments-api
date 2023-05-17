package uk.gov.justice.digital.assessments.jpa.entities.refdata

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import uk.gov.justice.digital.assessments.jpa.entities.AssessmentType
import java.io.Serializable

@Entity
@Table(name = "clone_assessment_excluded_questions", schema = "hmppsassessmentsschemas")
class CloneAssessmentExcludedQuestionsEntity(
  @Id
  @Column(name = "clone_assessment_excluded_questions_id")
  val cloneAssessmentExcludedQuestionsId: Long,

  @Column(name = "assessment_type")
  @Enumerated(EnumType.STRING)
  val assessmentType: AssessmentType,

  @Column(name = "question_code")
  val questionCode: String,

) : Serializable
